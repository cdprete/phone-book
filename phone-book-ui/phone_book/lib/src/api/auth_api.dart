import 'dart:async';

import 'package:dio/dio.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:retrofit/retrofit.dart';

part 'auth_api.freezed.dart';
part 'auth_api.g.dart';

@RestApi()
@lazySingleton
abstract class AuthApi {
  static const _contextPath = 'auth';
  static const _apiVersion = 'v1';
  static const _baseUrl = '$_contextPath/$_apiVersion';

  @factoryMethod
  factory AuthApi(Dio client) = _AuthApi;

  @MultiPart()
  @POST('$_baseUrl/login')
  Future<String> login({
    @Part() required String username,
    @Part() required String password,
  });

  @MultiPart()
  @POST('$_baseUrl/register')
  Future<void> register({
    @Part() required String username,
    @Part() required String password,
  });
}

@freezed
class AuthError with _$AuthError {
  const factory AuthError.unauthorized() = AuthErrorUnauthorized;
  const factory AuthError.userAlreadyRegistered() =
      AuthErrorUserAlreadyRegistered;
  const factory AuthError.unexpectedError({
    String? message,
    StackTrace? stacktrace,
  }) = AuthErrorUnexpecteError;
}
