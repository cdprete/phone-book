part of 'settings_bloc.dart';

@freezed
sealed class SettingsState with _$SettingsState {
  const factory SettingsState({
    @Default(false) bool isFetchingSettings,
    @Default(None()) Option<Settings> settings,
    @Default(None()) Option<SettingsError> error,
  }) = _SettingsState;
}
