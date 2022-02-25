import 'package:another_flushbar/flushbar_helper.dart';
import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:phone_book/src/di/injector.dart';
import 'package:phone_book/src/registration/bloc/registration_bloc.dart';

class RegistrationView extends StatefulWidget {
  const RegistrationView({Key? key}) : super(key: key);

  @override
  _RegistrationViewState createState() => _RegistrationViewState();
}

class _RegistrationViewState extends State<RegistrationView> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _usernameTextController = TextEditingController();
  final TextEditingController _passwordTextController = TextEditingController();
  final TextEditingController _confirmPasswordTextController =
      TextEditingController();

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;

    return LayoutBuilder(
      builder: (context, constraints) => BlocProvider<RegistrationBloc>(
        create: (context) => getIt.get(),
        child: SafeArea(
          child: Container(
            color: Theme.of(context).primaryColor.withAlpha(100),
            child: Center(
              child: ConstrainedBox(
                constraints: BoxConstraints(
                  maxWidth: constraints.maxWidth / 2,
                ),
                child: Card(
                  elevation: 5,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Form(
                      key: _formKey,
                      child: BlocConsumer<RegistrationBloc, RegistrationState>(
                        listener: (context, state) async {
                          final t = AppLocalizations.of(context)!;
                          if (state.registrationError.isSome()) {
                            state.registrationError
                                .fold(
                                  () => null,
                                  (err) => err.whenOrNull(
                                    userAlreadyRegistered: () =>
                                        FlushbarHelper.createError(
                                      message: t
                                          .registrationUserAlreadyRegisteredError,
                                    ),
                                    unexpectedError: (message, _) =>
                                        FlushbarHelper.createError(
                                      message: message!,
                                    ),
                                  ),
                                )
                                ?.show(context);
                          } else if (!state.isRegistering) {
                            context.router.pop();
                          }
                        },
                        builder: (context, state) {
                          return Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              _buildTitle(t),
                              _buildUsernameField(context, t, state),
                              _buildPasswordField(context, t, state),
                              _buildConfirmPasswordField(context, t, state),
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
    for (var controller in [
      _usernameTextController,
      _passwordTextController,
      _confirmPasswordTextController
    ]) {
      controller.dispose();
    }
    super.dispose();
  }

  Widget _buildTitle(AppLocalizations t) => Text(
        t.registrationTitle,
        style: const TextStyle(
          fontSize: 30,
          fontWeight: FontWeight.bold,
          overflow: TextOverflow.ellipsis,
        ),
      );

  Widget _buildUsernameField(
    BuildContext context,
    AppLocalizations t,
    RegistrationState state,
  ) =>
      TextFormField(
        controller: _usernameTextController,
        keyboardType: TextInputType.text,
        autocorrect: false,
        textInputAction: TextInputAction.next,
        enableSuggestions: false,
        maxLength: 255,
        enabled: !state.isRegistering,
        decoration: InputDecoration(
          labelText: t.registrationUsernameLabel,
        ),
        onFieldSubmitted: _onFieldSubmitted(context, state),
        validator: (value) => _validateFormField(
          value: value,
          nullErrorMessage: t.nullRegistrationUsernameError,
          blankErrorMessage: t.blankRegistrationUsernameError,
        ),
      );

  Widget _buildPasswordField(
    BuildContext context,
    AppLocalizations t,
    RegistrationState state,
  ) =>
      TextFormField(
        controller: _passwordTextController,
        keyboardType: TextInputType.text,
        obscureText: true,
        autocorrect: false,
        textInputAction: TextInputAction.next,
        enableSuggestions: false,
        maxLength: 255,
        enabled: !state.isRegistering,
        decoration: InputDecoration(
          labelText: t.registrationPasswordLabel,
        ),
        onFieldSubmitted: _onFieldSubmitted(context, state),
        validator: (value) {
          var error = _validateFormField(
            value: value,
            nullErrorMessage: t.nullRegistrationPasswordError,
            blankErrorMessage: t.blankRegistrationPasswordError,
          );
          if (error == null && value != _confirmPasswordTextController.text) {
            error = t.registrationPasswordsMismatchError;
          }

          return error;
        },
      );

  Widget _buildConfirmPasswordField(
    BuildContext context,
    AppLocalizations t,
    RegistrationState state,
  ) =>
      Container(
        margin: const EdgeInsets.only(bottom: 30),
        child: TextFormField(
          controller: _confirmPasswordTextController,
          keyboardType: TextInputType.text,
          obscureText: true,
          autocorrect: false,
          textInputAction: TextInputAction.done,
          enableSuggestions: false,
          maxLength: 255,
          enabled: !state.isRegistering,
          decoration: InputDecoration(
            labelText: t.registrationPasswordConfirmationLabel,
          ),
          onFieldSubmitted: _onFieldSubmitted(context, state),
          validator: (value) {
            var error = _validateFormField(
              value: value,
              nullErrorMessage: t.nullRegistrationPasswordError,
              blankErrorMessage: t.blankRegistrationPasswordError,
            );
            if (error == null && value != _passwordTextController.text) {
              error = t.registrationPasswordsMismatchError;
            }

            return error;
          },
        ),
      );

  Widget _buildFormButtons(
          BuildContext context, AppLocalizations t, RegistrationState state) =>
      Align(
        alignment: Alignment.centerRight,
        child: state.isRegistering
            ? const CircularProgressIndicator()
            : ElevatedButton(
                onPressed: () => _onSubmit(context),
                child: Text(
                  t.registrationButtonLabel,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
      );

  ValueChanged<String>? _onFieldSubmitted(
          BuildContext context, RegistrationState state) =>
      state.isRegistering ? null : (_) => _onSubmit(context);

  void _onSubmit(BuildContext context) {
    if (_formKey.currentState!.validate()) {
      context.read<RegistrationBloc>().register(
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
