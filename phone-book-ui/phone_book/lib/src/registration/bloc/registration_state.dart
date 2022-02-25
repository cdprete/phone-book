part of 'registration_bloc.dart';

@freezed
class RegistrationState with _$RegistrationState {
  const factory RegistrationState({
    @Default(false) bool isRegistering,
    @Default(None()) Option<AuthError> registrationError,
  }) = _RegistrationState;

  factory RegistrationState.initial() => const RegistrationState();
}
