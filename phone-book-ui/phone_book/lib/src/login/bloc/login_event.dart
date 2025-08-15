part of 'login_bloc.dart';

@freezed
sealed class LoginEvent with _$LoginEvent {
  const factory LoginEvent.login({
    required String username,
    required String password,
  }) = LoginEventLogin;
}
