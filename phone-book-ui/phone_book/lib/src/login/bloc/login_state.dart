part of 'login_bloc.dart';

@freezed
class LoginState with _$LoginState {
  const factory LoginState({
    @Default(false) bool isLoggingIn,
    @Default(None()) Option<AuthError> loginError,
  }) = _LoginState;

  factory LoginState.initial() => const LoginState();
}
