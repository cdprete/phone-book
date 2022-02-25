import 'package:another_flushbar/flushbar_helper.dart';
import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:phone_book/src/di/injector.dart';
import 'package:phone_book/src/infrastructure/app_router.dart';
import 'package:phone_book/src/login/bloc/login_bloc.dart';

class LoginView extends StatefulWidget {
  const LoginView({Key? key}) : super(key: key);

  @override
  _LoginViewState createState() => _LoginViewState();
}

class _LoginViewState extends State<LoginView> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _usernameTextController = TextEditingController();
  final TextEditingController _passwordTextController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;

    return LayoutBuilder(
      builder: (context, constraints) => BlocProvider<LoginBloc>(
        create: (context) => getIt.get(),
        child: SafeArea(
          child: Container(
            color: Theme.of(context).primaryColor.withAlpha(100),
            child: Center(
              child: ConstrainedBox(
                constraints: BoxConstraints(maxWidth: constraints.maxWidth / 2),
                child: Card(
                  elevation: 5,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Form(
                      key: _formKey,
                      child: BlocConsumer<LoginBloc, LoginState>(
                        listener: (context, state) async {
                          final t = AppLocalizations.of(context)!;
                          if (state.loginError.isSome()) {
                            state.loginError
                                .fold(
                                  () => null,
                                  (err) => err.whenOrNull(
                                    unauthorized: () =>
                                        FlushbarHelper.createError(
                                      message: t.loginFailed,
                                    ),
                                    unexpectedError: (message, _) =>
                                        FlushbarHelper.createError(
                                      message: message!,
                                    ),
                                  ),
                                )
                                ?.show(context);
                          } else if (!state.isLoggingIn) {
                            context.router.pushAndPopUntil(
                              const ContactsPageRoute(),
                              predicate: (r) => false,
                            );
                          }
                        },
                        builder: (context, state) {
                          return Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              _buildTitle(t),
                              _buildUsernameField(context, t, state),
                              _buildPasswordField(context, t, state),
                              _buildFormButtons(context, t, state),
                            ],
                          );
                        },
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _usernameTextController.dispose();
    _passwordTextController.dispose();
    super.dispose();
  }

  Widget _buildTitle(AppLocalizations t) => Text(
        t.loginTitle,
        style: const TextStyle(
          fontSize: 30,
          fontWeight: FontWeight.bold,
          overflow: TextOverflow.ellipsis,
        ),
      );

  Widget _buildUsernameField(
    BuildContext context,
    AppLocalizations t,
    LoginState state,
  ) =>
      TextFormField(
        controller: _usernameTextController,
        keyboardType: TextInputType.text,
        autocorrect: false,
        textInputAction: TextInputAction.next,
        enableSuggestions: false,
        maxLength: 255,
        enabled: !state.isLoggingIn,
        decoration: InputDecoration(
          labelText: t.loginUsernameLabel,
        ),
        validator: (value) => _validateFormField(
          value: value,
          nullErrorMessage: t.nullLoginUsernameError,
          blankErrorMessage: t.blankLoginUsernameError,
        ),
        onFieldSubmitted: _onFieldSubmitted(context, state),
      );

  Widget _buildPasswordField(
    BuildContext context,
    AppLocalizations t,
    LoginState state,
  ) =>
      Container(
        margin: const EdgeInsets.only(bottom: 30),
        child: TextFormField(
          controller: _passwordTextController,
          keyboardType: TextInputType.text,
          obscureText: true,
          autocorrect: false,
          textInputAction: TextInputAction.done,
          enableSuggestions: false,
          maxLength: 255,
          enabled: !state.isLoggingIn,
          decoration: InputDecoration(
            labelText: t.loginPasswordLabel,
          ),
          validator: (value) => _validateFormField(
            value: value,
            nullErrorMessage: t.nullLoginPasswordError,
            blankErrorMessage: t.blankLoginPasswordError,
          ),
          onFieldSubmitted: _onFieldSubmitted(context, state),
        ),
      );

  Widget _buildFormButtons(
    BuildContext context,
    AppLocalizations t,
    LoginState state,
  ) =>
      Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Flexible(
            child: ElevatedButton(
              onPressed: state.isLoggingIn
                  ? null
                  : () async {
                      var result = await context.router.push(
                        const RegistrationViewRoute(),
                      );
                      if (result == null) {
                        FlushbarHelper.createSuccess(
                          message: t.registrationSuccessfulMessage,
                        ).show(context);
                      }
                    },
              child:
                  Text(t.registerButtonLabel, overflow: TextOverflow.ellipsis),
            ),
          ),
          Flexible(
            child: Container(
              margin: const EdgeInsets.only(left: 16),
              child: state.isLoggingIn
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: () => _onSubmit(context),
                      child: Text(
                        t.loginButtonLabel,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
            ),
          ),
        ],
      );

  ValueChanged<String>? _onFieldSubmitted(
    BuildContext context,
    LoginState state,
  ) =>
      state.isLoggingIn ? null : (_) => _onSubmit(context);

  void _onSubmit(BuildContext context) {
    if (_formKey.currentState!.validate()) {
      context.read<LoginBloc>().login(
            username: _usernameTextController.text,
            password: _passwordTextController.text,
          );
    }
  }

  static String? _validateFormField({
    String? value,
    required String nullErrorMessage,
    required String blankErrorMessage,
  }) =>
      value == null
          ? nullErrorMessage
          : value.trim().isEmpty
              ? blankErrorMessage
              : null;
}
