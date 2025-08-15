part of 'registration_bloc.dart';

@freezed
sealed class RegistrationEvent with _$RegistrationEvent {
  const factory RegistrationEvent.register({
    required String username,
    required String password,
  }) = RegistrationEventRegistration;
}
