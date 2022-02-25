import 'dart:async';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/infrastructure/authentication_token_holder.dart';

const authClient = Named('authClient');
const apiBaseUrlQualifier = Named('apiBaseUrl');

@module
abstract class HttpClientModule {
  @LazySingleton(dispose: disposeDioClient)
  Dio getHttpClient(@apiBaseUrlQualifier String apiBaseUrl) =>
      Dio(BaseOptions(baseUrl: apiBaseUrl, responseType: ResponseType.json));

  @authClient
  @LazySingleton(dispose: disposeDioClient)
  Dio getHttpAuthClient(
    AuthenticationTokenHolder tokenHolder,
    @apiBaseUrlQualifier String apiBaseUrl,
  ) =>
      Dio(BaseOptions(baseUrl: apiBaseUrl, responseType: ResponseType.json))
        /*..interceptors.add(LogInterceptor(
          requestBody: true,
          responseBody: true,
        ))*/
        ..interceptors.add(InterceptorsWrapper(
          onRequest: (options, handler) {
            options.headers[HttpHeaders.authorizationHeader] =
                tokenHolder.token;
            handler.next(options);
          },
        ));

  @lazySingleton
  @apiBaseUrlQualifier
  String get apiBaseUrl => 'http://localhost/';
}

FutureOr<void> disposeDioClient(Dio client) async => client.close();
