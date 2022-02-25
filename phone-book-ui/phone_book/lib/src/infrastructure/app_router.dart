import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/contacts/contacts_page.dart';
import 'package:phone_book/src/login/login_view.dart';
import 'package:phone_book/src/registration/registration_view.dart';

part 'app_router.gr.dart';

@MaterialAutoRouter(
  routes: [
    AutoRoute(page: LoginView, initial: true),
    AutoRoute(page: RegistrationView),
    AutoRoute(page: ContactsPage),
  ],
)
@lazySingleton
class AppRouter extends _$AppRouter {}
