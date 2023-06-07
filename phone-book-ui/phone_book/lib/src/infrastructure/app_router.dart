import 'package:auto_route/auto_route.dart';
import 'package:injectable/injectable.dart';
import 'package:phone_book/src/contacts/contacts_page.dart';
import 'package:phone_book/src/login/login_view.dart';
import 'package:phone_book/src/registration/registration_view.dart';

part 'app_router.gr.dart';

@lazySingleton
@AutoRouterConfig()
class AppRouter extends _$AppRouter {
  @override
  RouteType get defaultRouteType => const RouteType.material();

  @override
  List<AutoRoute> get routes => [
        AutoRoute(page: LoginViewRoute.page, initial: true),
        AutoRoute(page: RegistrationViewRoute.page),
        AutoRoute(page: ContactsPageRoute.page),
      ];
}
