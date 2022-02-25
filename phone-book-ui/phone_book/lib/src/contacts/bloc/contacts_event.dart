part of 'contacts_bloc.dart';

@freezed
class ContactsEvent with _$ContactsEvent {
  const factory ContactsEvent.startCreateContact() =
      ContactsEventStartCreateContact;

  const factory ContactsEvent.createContact(Contact contact) =
      ContactsEventCreateContact;

  const factory ContactsEvent.abortContactCreation() =
      ContactsEventAbortContactCreation;

  const factory ContactsEvent.startEditContact(Contact contact) =
      ContactsEventStartEditContact;

  const factory ContactsEvent.editContact({
    required String id,
    required Contact contact,
  }) = ContactsEventEditContact;

  const factory ContactsEvent.abortContactEditing() =
      ContactsEventAbortContactEditing;

  const factory ContactsEvent.deleteContact(String id) =
      ContactsEventDeleteContact;

  const factory ContactsEvent.fetchContacts({
    required int page,
    @Default(20) int pageSize,
    @Default(null) String? searchValue,
  }) = ContactsEventFetchContacts;

  const factory ContactsEvent.fetchContact(String id) =
      ContactsEventFetchContact;

  const factory ContactsEvent.selectImage({@Default(null) int? maxSize}) =
      ContactsEventSelectImage;
}
