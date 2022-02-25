import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/api/settings_api.dart';

part 'settings_bloc.freezed.dart';
part 'settings_event.dart';
part 'settings_state.dart';

@injectable
class SettingsBloc extends Bloc<SettingsEvent, SettingsState> {
  final SettingsApi _settingsApi;

  SettingsBloc(SettingsApi settingsApi)
      : _settingsApi = settingsApi,
        super(const SettingsState()) {
    on<SettingsEventFetchSettings>(_onFetchSettings);
  }

  void fetchSettings() => add(const SettingsEvent.fetchSettings());

  void _onFetchSettings(
    SettingsEventFetchSettings event,
    Emitter<SettingsState> emit,
  ) async {
    emit(const SettingsState(isFetchingSettings: true));

    try {
      emit(SettingsState(settings: some(await _settingsApi.getSettings())));
    } on Exception catch (e, stacktrace) {
      emit(SettingsState(error: some(_mapException(e, stacktrace))));
    }
  }

  SettingsError _mapException(Exception e, StackTrace s) => e is DioError
      ? e.response?.statusCode == 401
          ? const SettingsError.unauthenticated()
          : SettingsError.unexpectedError(
              message: e.response?.data?.toString(),
              stackTrace: s,
            )
      : SettingsError.unexpectedError(message: e.toString(), stackTrace: s);
}
