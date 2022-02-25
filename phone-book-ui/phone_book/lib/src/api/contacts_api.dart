import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:http_parser/http_parser.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/backend_api.dart';
import 'package:phone_book/src/di/http_client_module.dart';
import 'package:retrofit/retrofit.dart';

part 'contacts_api.freezed.dart';
part 'contacts_api.g.dart';

@lazySingleton
@RestApi(parser: Parser.FlutterCompute)
abstract class ContactsApi extends BackendApi {
  static const contactsUrl = '${BackendApi.baseUrl}/contacts';
  static const singleContactUrl = '${BackendApi.baseUrl}/contacts/{id}';

  @factoryMethod
  factory ContactsApi(@authClient Dio client) = _ContactsApi;

  @GET(contactsUrl)
  Future<HttpResponse<List<Contact>>> getContacts({
    @Query('page') required int page,
    @Query('size') int? pageSize = 20,
    @Query('q') String? searchValue,
  });

  @GET(singleContactUrl)
  Future<Contact> getContact(@Path() String id);

  @DELETE(singleContactUrl)
  Future<void> deleteContact(@Path() String id);

  @POST(contactsUrl)
  Future<HttpResponse<void>> createContact(@Body() Contact contact);

  @PUT(singleContactUrl)
  Future<void> updateContact({
    @Path() required String id,
    @Body() required Contact contact,
  });

  @MultiPart()
  @POST('$singleContactUrl/images')
  Future<void> uploadContactImage({
    @Path() required String id,
    @Part(fileName: 'image', contentType: 'image/*') required List<int> image,
  });
}

FutureOr<Contact> deserializeContact(Map<String, dynamic> json) =>
    Contact.fromJson(json);
FutureOr<Map<String, dynamic>> serializeContact(Contact object) =>
    object.toJson();
FutureOr<List<Contact>> deserializeContactList(
        List<Map<String, dynamic>> json) =>
    json.map((c) => Contact.fromJson(c)).toList();
FutureOr<List<Map<String, dynamic>>> serializeContactList(
        List<Contact> contacts) =>
    contacts.map((c) => c.toJson()).toList();

@freezed
class Contact with _$Contact {
  const Contact._();

  @JsonSerializable(explicitToJson: true)
  factory Contact({
    String? id,
    String? name,
    String? surname,
    @JsonKey(toJson: Contact.ignore) @Uint8ListConverter() Uint8List? image,
    @Default({}) Set<EmailAddress> emailAddresses,
    @Default({}) Set<PhoneNumber> phoneNumbers,
  }) = _Contact;

  factory Contact.fromJson(Map<String, dynamic> json) =>
      _$ContactFromJson(json);

  String get fullName => [name, surname]
      .where((value) => value != null)
      .map((value) => value!.trim())
      .where((value) => value.isNotEmpty)
      .join(' ');

  String get initials => [name, surname]
      .where((value) => value != null)
      .map((value) => value!.trim())
      .where((value) => value.isNotEmpty)
      .map((value) => value[0].toUpperCase())
      .join();

  static T? ignore<T>(dynamic _) => null;
}

@freezed
class EmailAddress with _$EmailAddress {
  factory EmailAddress({
    required String emailAddress,
    required EmailAddressType type,
  }) = _EmailAddress;

  factory EmailAddress.fromJson(Map<String, dynamic> json) =>
      _$EmailAddressFromJson(json);
}

@JsonEnum()
enum EmailAddressType {
  @JsonValue('HOME')
  home,
  @JsonValue('OFFICE')
  office,
  @JsonValue('OTHER')
  other
}

extension EmailAddressTypeExtension on EmailAddressType {
  String translate(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    switch (this) {
      case EmailAddressType.office:
        return t.emailAddressTypeOfficeLabel;
      case EmailAddressType.home:
        return t.emailAddressTypeHomeLabel;
      case EmailAddressType.other:
        return t.emailAddressTypeOtherLabel;
    }
  }
}

@freezed
class PhoneNumber with _$PhoneNumber {
  factory PhoneNumber({
    required String phoneNumber,
    required PhoneNumberType type,
  }) = _PhoneNumber;

  factory PhoneNumber.fromJson(Map<String, dynamic> json) =>
      _$PhoneNumberFromJson(json);
}

@JsonEnum()
enum PhoneNumberType {
  @JsonValue('MOBILE')
  mobile,
  @JsonValue('HOME')
  home,
  @JsonValue('OFFICE')
  office,
  @JsonValue('OTHER')
  other
}

extension PhoneNumberTypeExtension on PhoneNumberType {
  String translate(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    switch (this) {
      case PhoneNumberType.office:
        return t.phoneNumberTypeOfficeLabel;
      case PhoneNumberType.home:
        return t.phoneNumberTypeHomeLabel;
      case PhoneNumberType.other:
        return t.phoneNumberTypeOtherLabel;
      case PhoneNumberType.mobile:
        return t.phoneNumberTypeMobileLabel;
    }
  }
}

class Uint8ListConverter implements JsonConverter<Uint8List?, String?> {
  const Uint8ListConverter();

  @override
  Uint8List? fromJson(String? json) => json == null ? null : base64Decode(json);

  @override
  String? toJson(Uint8List? object) => base64Encode(object?.toList() ?? []);
}

@freezed
class ContactsError with _$ContactsError {
  const factory ContactsError.unauthenticated() = ContactsErrorUnauthenticated;
  const factory ContactsError.notAnImage() = ContactsErrorNotAnImage;
  const factory ContactsError.notFound() = ContactsErrorNotFound;
  const factory ContactsError.duplicateEmailAddress() =
      ContactsErrorDuplicateEmailAddress;
  const factory ContactsError.duplicatePhoneNumber() =
      ContactsErrorDuplicatePhoneNumber;
  const factory ContactsError.missingInformation() =
      ContactsErrorMissingInformation;
  const factory ContactsError.unexpectedError({
    String? message,
    StackTrace? stacktrace,
  }) = ContactsErrorUnexpectedError;
}

@freezed
class ErrorResponse with _$ErrorResponse {
  factory ErrorResponse({
    required String code,
    required String defaultMessage,
  }) = _ErrorResponse;

  factory ErrorResponse.fromJson(Map<String, dynamic> json) =>
      _$ErrorResponseFromJson(json);
}
