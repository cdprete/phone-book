import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/auth_api.dart';

part 'registration_bloc.freezed.dart';
part 'registration_event.dart';
part 'registration_state.dart';

@injectable
class RegistrationBloc extends Bloc<RegistrationEvent, RegistrationState> {
  final AuthApi _authApi;

  RegistrationBloc(AuthApi authApi)
      : _authApi = authApi,
        super(RegistrationState.initial()) {
    on<RegistrationEventRegistration>(_onRegister);
  }

  void register({
    required String username,
    required String password,
  }) {
    add(RegistrationEvent.register(username: username, password: password));
  }

  void _onRegister(RegistrationEventRegistration event,
      Emitter<RegistrationState> emit) async {
    emit(state.copyWith(isRegistering: true, registrationError: none()));

    AuthError? error;
    try {
      await _authApi.register(
        username: event.username,
        password: event.password,
      );
    } on Exception catch (e, stacktrace) {
      error = _mapException(e, stacktrace);
    }

    emit(state.copyWith(
      isRegistering: false,
      registrationError: optionOf(error),
    ));
  }

  AuthError _mapException(Exception e, StackTrace s) => e is DioError
      ? e.response?.statusCode == 409
          ? const AuthError.userAlreadyRegistered()
          : AuthError.unexpectedError(
              message: e.response?.data?.toString(),
              stacktrace: s,
            )
      : AuthError.unexpectedError(message: e.toString(), stacktrace: s);
}
