import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/contacts_api.dart';
import 'package:phone_book/src/infrastructure/image_picker.dart';
import 'package:retrofit/retrofit.dart';

part 'contacts_bloc.freezed.dart';
part 'contacts_event.dart';
part 'contacts_state.dart';

@injectable
class ContactsBloc extends Bloc<ContactsEvent, ContactsState> {
  final ContactsApi _contactsApi;

  ContactsBloc(ContactsApi contactsApi)
      : _contactsApi = contactsApi,
        super(const ContactsState()) {
    on<ContactsEventFetchContacts>(_onFetchContacts);
    on<ContactsEventFetchContact>(_onFetchContact);
    on<ContactsEventStartCreateContact>(_onStartCreateContact);
    on<ContactsEventCreateContact>(_onCreateContact);
    on<ContactsEventAbortContactCreation>(_onAbortContactCreation);
    on<ContactsEventDeleteContact>(_onDeleteContact);
    on<ContactsEventStartEditContact>(_onStartEditContact);
    on<ContactsEventEditContact>(_onEditContact);
    on<ContactsEventAbortContactEditing>(_onAbortContactEditing);
    on<ContactsEventSelectImage>(_onSelectImage);
  }

  void selectImage({int? maxSize}) {
    add(ContactsEvent.selectImage(maxSize: maxSize));
  }

  void _onSelectImage(
    ContactsEventSelectImage event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      selectContactImage:
          const _SelectContactImage(isSelectingContactImage: true),
    ));

    final result = await pickImage(event.maxSize);

    return result.fold(
      (err) => emit(state.copyWith(
        selectContactImage: _SelectContactImage(error: some(err)),
      )),
      (img) => emit(
        state.copyWith(
          selectContactImage: _SelectContactImage(contactImage: optionOf(img)),
          fetchSingleContact: state.fetchSingleContact.copyWith(
            contact: state.fetchSingleContact.contact
                .map((c) => c.copyWith(image: img)),
          ),
        ),
      ),
    );
  }

  void deleteContact(Contact contact) {
    add(ContactsEvent.deleteContact(contact.id!));
  }

  void _onDeleteContact(
    ContactsEventDeleteContact event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      deleteContact: const _DeleteContact(isDeletingContact: true),
    ));

    try {
      await _contactsApi.deleteContact(event.id);

      emit(state.copyWith(
        deleteContact: const _DeleteContact(deleteContactSuccess: true),
        fetchSingleContact: _FetchSingleContact(contact: none()),
      ));
      emit(state.copyWith(deleteContact: const _DeleteContact()));
    } on Exception catch (e, s) {
      emit(state.copyWith(
        deleteContact: _DeleteContact(
          error: some(await _mapException(e, s, _mapDeleteContactException)),
        ),
      ));
    }
  }

  ContactsError _mapDeleteContactException(DioError e, StackTrace s) {
    switch (e.response?.statusCode) {
      case 401:
        return const ContactsError.unauthenticated();
      case 404:
        return const ContactsError.notFound();
    }

    return _toFallbackError(e, s);
  }

  ContactsError _toFallbackError(DioError e, StackTrace s) =>
      ContactsError.unexpectedError(
        message: e.response?.data?.toString(),
        stacktrace: s,
      );

  void fetchContacts({int page = Page.firstPageNumber, int pageSize = 20}) {
    add(ContactsEvent.fetchContacts(page: page, pageSize: pageSize));
  }

  void _onFetchContacts(
    ContactsEventFetchContacts event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      fetchContacts: const _FetchContacts(isFetchingContacts: true),
    ));

    try {
      final fetchResult = await _contactsApi.getContacts(
        page: event.page,
        pageSize: event.pageSize,
        searchValue: event.searchValue,
      );

      emit(state.copyWith(
        fetchContacts: _FetchContacts(
          contactsPage: _mapFetchToPage(
            event.page,
            fetchResult,
          ),
        ),
      ));
    } on Exception catch (e, s) {
      return emit(state.copyWith(
        fetchContacts: _FetchContacts(
          error: some(await _mapException(e, s, _mapFetchContactsException)),
        ),
      ));
    }
  }

  Page<Contact> _mapFetchToPage(int page, HttpResponse<List<Contact>> result) {
    final links = result.response.headers['Link']!.first;

    return Page(
      hasPreviousPage: links.contains('rel="previous"'),
      hasNextPage: links.contains('rel="next"'),
      items: result.data.toSet(),
      currentPage: page,
    );
  }

  FutureOr<ContactsError> _mapException(
    Exception e,
    StackTrace s,
    FutureOr<ContactsError> Function(DioError, StackTrace) mapDioError,
  ) async =>
      e is DioError
          ? await mapDioError.call(e, s)
          : ContactsError.unexpectedError(message: e.toString(), stacktrace: s);

  FutureOr<ContactsError> _mapFetchContactsException(
          DioError e, StackTrace s) =>
      e.response?.statusCode == 401
          ? const ContactsError.unauthenticated()
          : _toFallbackError(e, s);

  void fetchContact(String id) {
    add(ContactsEvent.fetchContact(id));
  }

  void _onFetchContact(
    ContactsEventFetchContact event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      fetchSingleContact: const _FetchSingleContact(
        isFetchingSingleContact: true,
      ),
    ));

    try {
      final contact = await _contactsApi.getContact(event.id);

      emit(state.copyWith(
        fetchSingleContact: _FetchSingleContact(contact: some(contact)),
      ));
    } on Exception catch (e, s) {
      emit(state.copyWith(
        fetchSingleContact: _FetchSingleContact(
            error: some(await _mapException(e, s, _mapFetchContactException))),
      ));
    }
  }

  ContactsError _mapFetchContactException(DioError e, StackTrace s) {
    switch (e.response?.statusCode) {
      case 401:
        return const ContactsError.unauthenticated();
      case 404:
        return const ContactsError.notFound();
    }

    return _toFallbackError(e, s);
  }

  void startCreateContact() {
    add(const ContactsEvent.startCreateContact());
  }

  void _onStartCreateContact(
    ContactsEventStartCreateContact event,
    Emitter<ContactsState> emit,
  ) {
    emit(state.copyWith(
      createContact: const _CreateContact(isCreatingContact: true),
    ));
  }

  void createContact(Contact contact) {
    add(ContactsEvent.createContact(contact));
  }

  void _onCreateContact(
    ContactsEventCreateContact event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      createContact: const _CreateContact(
        isCreatingContact: true,
        isSubmittingContactCreation: true,
      ),
    ));

    try {
      final result = await _contactsApi.createContact(event.contact);

      var location = result.response.headers[HttpHeaders.locationHeader]!.first;
      final id = location.substring(location.lastIndexOf('/') + 1);

      if (event.contact.image != null) {
        await _contactsApi.uploadContactImage(
          id: id,
          image: event.contact.image!.toList(),
        );
      }

      emit(state.copyWith(
        createContact: const _CreateContact(createContactSuccess: true),
      ));
      fetchContact(id);
      abortContactCreation();
    } on Exception catch (e, s) {
      emit(state.copyWith(
        createContact: _CreateContact(
          isCreatingContact: true,
          error: some(await _mapException(e, s, _mapCreateContactException)),
        ),
      ));
    }
  }

  FutureOr<ContactsError> _mapCreateContactException(DioError e, StackTrace s) {
    switch (e.response?.statusCode) {
      case 400:
        return _extractErrorFromBadRequest(e, s);
      case 401:
        return const ContactsError.unauthenticated();
    }

    return _toFallbackError(e, s);
  }

  void abortContactCreation() {
    add(const ContactsEvent.abortContactCreation());
  }

  void _onAbortContactCreation(
    ContactsEventAbortContactCreation event,
    Emitter<ContactsState> emit,
  ) {
    emit(state.copyWith(
      createContact: const _CreateContact(),
      selectContactImage: const _SelectContactImage(),
    ));
  }

  void startEditContact(Contact contact) {
    add(ContactsEvent.startEditContact(contact));
  }

  void _onStartEditContact(
    ContactsEventStartEditContact event,
    Emitter<ContactsState> emit,
  ) {
    emit(state.copyWith(
      editContact: const _EditContact(isEditingContact: true),
    ));
  }

  void editContact(Contact contact) {
    add(ContactsEvent.editContact(id: contact.id!, contact: contact));
  }

  void _onEditContact(
    ContactsEventEditContact event,
    Emitter<ContactsState> emit,
  ) async {
    emit(state.copyWith(
      editContact: const _EditContact(
        isEditingContact: true,
        isSubmittingContactEdit: true,
      ),
    ));

    try {
      await _contactsApi.updateContact(id: event.id, contact: event.contact);
      if (event.contact.image != null) {
        await _contactsApi.uploadContactImage(
          id: event.id,
          image: event.contact.image!.toList(),
        );
      }

      emit(state.copyWith(
        editContact: const _EditContact(editContactSuccess: true),
      ));
      fetchContact(event.id);
      abortContactEditing();
    } on Exception catch (e, s) {
      emit(state.copyWith(
        editContact: _EditContact(
          isEditingContact: true,
          error: some(await _mapException(e, s, _mapEditContactException)),
        ),
      ));
    }
  }

  FutureOr<ContactsError> _mapEditContactException(DioError e, StackTrace s) =>
      _mapCreateContactException(e, s);

  void abortContactEditing() {
    add(const ContactsEvent.abortContactEditing());
  }

  void _onAbortContactEditing(
    ContactsEventAbortContactEditing event,
    Emitter<ContactsState> emit,
  ) {
    emit(state.copyWith(
      editContact: const _EditContact(),
      selectContactImage: const _SelectContactImage(),
    ));
  }

  FutureOr<ContactsError> _extractErrorFromBadRequest(
    DioError e,
    StackTrace s,
  ) async {
    final errorResponse = await compute(
        ErrorResponse.fromJson, e.response!.data as Map<String, dynamic>);
    switch (errorResponse.code) {
      case 'duplicate.email.addresses':
        return const ContactsError.duplicateEmailAddress();
      case 'duplicate.phone.numbers':
        return const ContactsError.duplicatePhoneNumber();
      case 'name.and.surname.blank':
        return const ContactsError.missingInformation();
      case 'not.an.image':
        return const ContactsError.notAnImage();
      case 'invalid.input.data':
        return ContactsError.unexpectedError(
          message: errorResponse.defaultMessage,
        );
      default:
        return _toFallbackError(e, s);
    }
  }
}

@freezed
class Page<T> with _$Page<T> {
  static const firstPageNumber = 1;

  const factory Page({
    @Default(false) bool hasPreviousPage,
    @Default(false) bool hasNextPage,
    @Default(Page.firstPageNumber) int currentPage,
    @Default({}) Set<T> items,
  }) = _Page<T>;
}
