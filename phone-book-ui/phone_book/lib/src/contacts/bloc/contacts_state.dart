part of 'contacts_bloc.dart';

@freezed
class ContactsState with _$ContactsState {
  const ContactsState._();

  const factory ContactsState({
    @Default(_DeleteContact()) _DeleteContact deleteContact,
    @Default(_FetchContacts()) _FetchContacts fetchContacts,
    @Default(_FetchSingleContact()) _FetchSingleContact fetchSingleContact,
    @Default(_SelectContactImage()) _SelectContactImage selectContactImage,
    @Default(_EditContact()) _EditContact editContact,
    @Default(_CreateContact()) _CreateContact createContact,
  }) = _ContactsState;
}

@freezed
class _DeleteContact with _$_DeleteContact {
  const _DeleteContact._();

  const factory _DeleteContact({
    @Default(false) bool isDeletingContact,
    @Default(false) bool deleteContactSuccess,
    @Default(None()) Option<ContactsError> error,
  }) = __DeleteContact;
}

@freezed
class _FetchContacts with _$_FetchContacts {
  const factory _FetchContacts({
    @Default(false) bool isFetchingContacts,
    @Default(Page()) Page<Contact> contactsPage,
    @Default(None()) Option<ContactsError> error,
  }) = __FetchContacts;
}

@freezed
class _FetchSingleContact with _$_FetchSingleContact {
  const factory _FetchSingleContact({
    @Default(false) bool isFetchingSingleContact,
    @Default(None()) Option<Contact> contact,
    @Default(None()) Option<ContactsError> error,
  }) = __FetchSingleContact;
}

@freezed
class _SelectContactImage with _$_SelectContactImage {
  const factory _SelectContactImage({
    @Default(false) bool isSelectingContactImage,
    @Default(None()) Option<ImagePickerError> error,
    @Default(None()) Option<Uint8List> contactImage,
  }) = __SelectContactImage;
}

@freezed
class _EditContact with _$_EditContact {
  const _EditContact._();

  const factory _EditContact({
    @Default(false) bool isEditingContact,
    @Default(false) bool isSubmittingContactEdit,
    @Default(false) bool editContactSuccess,
    @Default(None()) Option<ContactsError> error,
  }) = __EditContact;
}

@freezed
class _CreateContact with _$_CreateContact {
  const _CreateContact._();

  const factory _CreateContact({
    @Default(false) bool isCreatingContact,
    @Default(false) bool isSubmittingContactCreation,
    @Default(false) bool createContactSuccess,
    @Default(None()) Option<ContactsError> error,
  }) = __CreateContact;
}
