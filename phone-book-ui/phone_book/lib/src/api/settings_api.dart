import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/backend_api.dart';
import 'package:phone_book/src/di/http_client_module.dart';
import 'package:retrofit/retrofit.dart';

part 'settings_api.freezed.dart';
part 'settings_api.g.dart';

@lazySingleton
@RestApi(parser: Parser.FlutterCompute)
abstract class SettingsApi extends BackendApi {
  @factoryMethod
  factory SettingsApi(@authClient Dio client) = _SettingsApi;

  @GET('${BackendApi.baseUrl}/settings')
  Future<Settings> getSettings();
}

FutureOr<Settings> deserializeSettings(Map<String, dynamic> json) =>
    Settings.fromJson(json);
FutureOr<Map<String, dynamic>> serializeSettings(Settings object) =>
    object.toJson();

@freezed
class Settings with _$Settings {
  factory Settings({
    required int maxImageSizeBytes,
  }) = _Settings;
  factory Settings.fromJson(Map<String, dynamic> json) =>
      _$SettingsFromJson(json);
}

@freezed
class SettingsError with _$SettingsError {
  const factory SettingsError.unauthenticated() = SettingsErrorUnauthenticated;
  const factory SettingsError.unexpectedError({
    String? message,
    StackTrace? stackTrace,
  }) = SettingsErrorUnexpectedError;
}
