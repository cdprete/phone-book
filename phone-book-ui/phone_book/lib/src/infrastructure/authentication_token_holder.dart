import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';

@lazySingleton
class AuthenticationTokenHolder {
  static const keyName = 'token';

  final SharedPreferences _prefs;

  AuthenticationTokenHolder(SharedPreferences prefs) : _prefs = prefs;

  String get token => _prefs.getString(keyName)!;

  set token(String token) => _prefs.setString(keyName, token);

  @disposeMethod
  Future<bool> clear() {
    return _prefs.clear();
  }
}
