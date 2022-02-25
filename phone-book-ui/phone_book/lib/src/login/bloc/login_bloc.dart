import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/auth_api.dart';
import 'package:phone_book/src/infrastructure/authentication_token_holder.dart';

part 'login_bloc.freezed.dart';
part 'login_event.dart';
part 'login_state.dart';

@injectable
class LoginBloc extends Bloc<LoginEvent, LoginState> {
  final AuthApi _authApi;
  final AuthenticationTokenHolder _tokenHolder;

  LoginBloc(
    AuthApi authApi,
    AuthenticationTokenHolder tokenHolder,
  )   : _authApi = authApi,
        _tokenHolder = tokenHolder,
        super(LoginState.initial()) {
    on<LoginEventLogin>(_onLogin);
  }

  void login({
    required String username,
    required String password,
  }) {
    add(LoginEvent.login(username: username, password: password));
  }

  void _onLogin(LoginEventLogin event, Emitter<LoginState> emit) async {
    emit(state.copyWith(isLoggingIn: true, loginError: none()));

    AuthError? error;
    try {
      _tokenHolder.token = await _authApi.login(
        username: event.username,
        password: event.password,
      );
    } on Exception catch (e, stacktrace) {
      error = _mapException(e, stacktrace);
    }

    emit(state.copyWith(isLoggingIn: false, loginError: optionOf(error)));
  }

  AuthError _mapException(Exception e, StackTrace s) => e is DioError
      ? e.response?.statusCode == 401
          ? const AuthError.unauthorized()
          : AuthError.unexpectedError(message: e.response?.data, stacktrace: s)
      : AuthError.unexpectedError(message: e.toString(), stacktrace: s);
}
