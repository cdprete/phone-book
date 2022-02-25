import 'dart:typed_data';

import 'package:another_flushbar/flushbar_helper.dart';
import 'package:dartz/dartz.dart' as f;
import 'package:email_validator/email_validator.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:phone_book/src/api/contacts_api.dart';
import 'package:phone_book/src/contacts/bloc/contacts_bloc.dart';
import 'package:phone_book/src/di/injector.dart';
import 'package:phone_book/src/settings/bloc/settings_bloc.dart';

part 'edit_contact_widget.freezed.dart';

class EditContactWidget extends StatefulWidget {
  // true = edit, false = create
  final bool edit;

  const EditContactWidget({Key? key, this.edit = true}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _EditContactsWidgetState();
}

class _EditContactsWidgetState extends State<EditContactWidget> {
  final formKey = GlobalKey<FormState>();
  final nameKey = GlobalKey<_GeneralInfoFieldState>();
  final surnameKey = GlobalKey<_GeneralInfoFieldState>();

  final List<_KeyTextAndType<_EmailAddressFieldState, EmailAddressType>>
      emailAddresses = [];
  final List<_KeyTextAndType<_PhoneNumberFieldState, PhoneNumberType>>
      phoneNumbers = [];
  final ScrollController scrollController = ScrollController();

  @override
  void initState() {
    final contact =
        context.read<ContactsBloc>().state.fetchSingleContact.contact;
    initListKeys<EmailAddress, EmailAddressType, _EmailAddressFieldState>(
      contact.map((c) => c.emailAddresses).getOrElse(() => {}),
      emailAddresses,
      (e) => e.emailAddress,
      (e) => e.type,
    );
    initListKeys<PhoneNumber, PhoneNumberType, _PhoneNumberFieldState>(
      contact.map((c) => c.phoneNumbers).getOrElse(() => {}),
      phoneNumbers,
      (p) => p.phoneNumber,
      (p) => p.type,
    );
    super.initState();
  }

  void initListKeys<E, T, S extends State<StatefulWidget>>(
    Iterable<E>? items,
    List<_KeyTextAndType<S, T>> keys,
    String Function(E) textGetter,
    T Function(E) typeGetter,
  ) {
    if (widget.edit) {
      for (E item in items ?? []) {
        keys.add(_KeyTextAndType(
          key: GlobalKey(),
          text: textGetter.call(item),
          type: typeGetter.call(item),
        ));
      }
    }
    if (keys.isEmpty) {
      keys.add(_KeyTextAndType(key: GlobalKey()));
    }
  }

  @override
  Widget build(BuildContext context) => BlocProvider<SettingsBloc>(
        create: (context) => getIt.get()..fetchSettings(),
        child: BlocConsumer<ContactsBloc, ContactsState>(
          listener: (context, state) {
            final t = AppLocalizations.of(context)!;
            if (widget.edit) {
              final editState = state.editContact;
              editState.error.fold(() {
                if (editState.editContactSuccess) {
                  FlushbarHelper.createSuccess(
                    message: t.editContactSuccessfulMessage,
                  ).show(context);
                }
              }, (err) => err.whenOrNull());
            } else {
              final createState = state.createContact;
              createState.error.fold(
                () {
                  if (createState.createContactSuccess) {
                    FlushbarHelper.createSuccess(
                      message: t.createContactSuccessfulMessage,
                    ).show(context);
                  }
                },
                (err) => err.whenOrNull(),
              );
            }
          },
          builder: (context, state) {
            final contact =
                context.read<ContactsBloc>().state.fetchSingleContact.contact;

            return Form(
              key: formKey,
              child: ListView(
                controller: scrollController,
                children: [
                  _ImageField(edit: widget.edit),
                  _GeneralInfoField(
                    key: nameKey,
                    otherKey: surnameKey,
                    initialValue: widget.edit
                        ? contact.map((c) => c.name).toNullable()
                        : null,
                    label: (t) => t.contactDetailsNameFieldName,
                    validationError: (t) => t.missingNameError,
                  ),
                  Container(
                    margin: const EdgeInsets.only(bottom: 50),
                    child: _GeneralInfoField(
                      key: surnameKey,
                      otherKey: nameKey,
                      initialValue: widget.edit
                          ? contact.map((c) => c.surname).toNullable()
                          : null,
                      label: (t) => t.contactDetailsSurnameFieldName,
                      validationError: (t) => t.missingSurnameError,
                    ),
                  ),
                  for (final emailAddress in emailAddresses)
                    _EmailAddressField(
                      key: emailAddress.key,
                      initialValue: emailAddress.text,
                      initialType: emailAddress.type,
                      onRemoveClick: (key) => onRemoveButtonClick(
                        emailAddresses,
                        key,
                      ),
                      otherEmailAddresses: emailAddresses
                          .map((e) => e.key)
                          .where((key) => key != emailAddress.key),
                    ),
                  _PlusButton(onClick: () => onPlusButtonClick(emailAddresses)),
                  for (final phoneNumber in phoneNumbers)
                    _PhoneNumberField(
                      key: phoneNumber.key,
                      initialValue: phoneNumber.text,
                      initialType: phoneNumber.type,
                      onRemoveClick: (key) => onRemoveButtonClick(
                        phoneNumbers,
                        key,
                      ),
                      otherPhoneNumbers: phoneNumbers
                          .map((p) => p.key)
                          .where((key) => key != phoneNumber.key),
                    ),
                  _PlusButton(onClick: () => onPlusButtonClick(phoneNumbers)),
                  _FormButtons(
                    onSubmit: () => onSubmit(context),
                    onCancel: () => onCancel(context),
                  ),
                ],
              ),
            );
          },
        ),
      );

  void onPlusButtonClick<S extends State<StatefulWidget>, T>(
    List<_KeyTextAndType<S, T>> targetList,
  ) {
    setState(() => targetList.add(_KeyTextAndType(key: GlobalKey())));
  }

  void onRemoveButtonClick<S extends State<StatefulWidget>, T>(
    List<_KeyTextAndType<S, T>> targetList,
    Key key,
  ) {
    int indexToRemove = 0;
    for (int i = 0; i < targetList.length; ++i) {
      if (targetList[i].key == key) {
        indexToRemove = i;
        break;
      }
    }
    setState(() => targetList.removeAt(indexToRemove));
  }

  void onSubmit(BuildContext context) {
    if (formKey.currentState?.validate() ?? false) {
      final bloc = context.read<ContactsBloc>();
      final contact = bloc.state.fetchSingleContact.contact;
      final Contact toSubmit = Contact(
        id: contact.map((c) => c.id).toNullable(),
        name: nameKey.currentState?.text,
        surname: surnameKey.currentState?.text,
        emailAddresses: emailAddresses
            .map((e) => e.key)
            .map((key) {
              final state = key.currentState!;

              return EmailAddress(
                emailAddress: state.emailAddress,
                type: state.type,
              );
            })
            .where((e) => e.emailAddress.isNotEmpty)
            .toSet(),
        phoneNumbers: phoneNumbers
            .map((p) => p.key)
            .map((key) {
              final state = key.currentState!;

              return PhoneNumber(
                phoneNumber: state.phoneNumber,
                type: state.type,
              );
            })
            .where((p) => p.phoneNumber.isNotEmpty)
            .toSet(),
        image: getContactImage(bloc.state).fold(() => null, (img) => img),
      );
      widget.edit ? bloc.editContact(toSubmit) : bloc.createContact(toSubmit);
    }
  }

  void onCancel(BuildContext context) => widget.edit
      ? context.read<ContactsBloc>().abortContactEditing()
      : context.read<ContactsBloc>().abortContactCreation();

  f.Option<Uint8List> getContactImage(ContactsState state) =>
      _getContactImage(state, widget.edit);

  @override
  void dispose() {
    scrollController.dispose();
    super.dispose();
  }
}

typedef _TranslateFunction = String Function(AppLocalizations);

class _EmailAddressField extends StatefulWidget {
  final void Function(Key) onRemoveClick;
  final String? initialValue;
  final EmailAddressType? initialType;
  final Iterable<GlobalKey<_EmailAddressFieldState>> otherEmailAddresses;

  const _EmailAddressField({
    required Key key,
    required this.onRemoveClick,
    this.initialValue,
    this.initialType,
    this.otherEmailAddresses = const [],
  }) : super(key: key);

  @override
  State<_EmailAddressField> createState() => _EmailAddressFieldState();
}

class _EmailAddressFieldState extends State<_EmailAddressField> {
  late final TextEditingController controller;
  late EmailAddressType type;

  @override
  void initState() {
    controller = TextEditingController(text: widget.initialValue);
    type = widget.initialType ?? EmailAddressType.home;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final AppLocalizations t = AppLocalizations.of(context)!;

    return Row(
      children: [
        Expanded(
          flex: 3,
          child: Container(
            margin: const EdgeInsets.only(right: 8),
            child: TextFormField(
              controller: controller,
              keyboardType: TextInputType.emailAddress,
              autocorrect: false,
              textInputAction: TextInputAction.next,
              enableSuggestions: false,
              maxLength: 255,
              decoration: InputDecoration(
                labelText: t.emailAddressInputTextLabel,
              ),
              validator: (value) => validate(t, value),
            ),
          ),
        ),
        Expanded(
          child: DropdownButton<EmailAddressType>(
            isExpanded: true,
            value: type,
            items: EmailAddressType.values
                .map((t) => DropdownMenuItem(
                      value: t,
                      child: Text(
                        t.translate(context),
                        overflow: TextOverflow.ellipsis,
                      ),
                    ))
                .toList(),
            onChanged: (t) => setState(() => type = t!),
          ),
        ),
        IconButton(
          onPressed: () => widget.onRemoveClick(widget.key!),
          icon: const Icon(Icons.remove),
        )
      ],
    );
  }

  String get emailAddress => controller.text.trim();

  String? validate(AppLocalizations t, String? value) {
    if (value == null) {
      return t.nullEmailAddressError;
    } else if (value.trim().isEmpty) {
      return t.blankEmailAddressError;
    } else if (!EmailValidator.validate(value)) {
      return t.notAnEmailAddressError;
    } else if (widget.otherEmailAddresses.any(
      (key) => key.currentState?.emailAddress == value.trim(),
    )) {
      return t.duplicateEmailAddressError;
    }

    return null;
  }
}

class _PhoneNumberField extends StatefulWidget {
  final void Function(Key) onRemoveClick;
  final String? initialValue;
  final PhoneNumberType? initialType;
  final Iterable<GlobalKey<_PhoneNumberFieldState>> otherPhoneNumbers;

  const _PhoneNumberField({
    required Key key,
    required this.onRemoveClick,
    this.initialValue,
    this.initialType,
    this.otherPhoneNumbers = const [],
  }) : super(key: key);

  @override
  State<_PhoneNumberField> createState() => _PhoneNumberFieldState();
}

class _PhoneNumberFieldState extends State<_PhoneNumberField> {
  late final TextEditingController controller;
  late PhoneNumberType type;

  @override
  void initState() {
    controller = TextEditingController(text: widget.initialValue);
    type = widget.initialType ?? PhoneNumberType.mobile;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final AppLocalizations t = AppLocalizations.of(context)!;

    return Row(
      children: [
        Expanded(
          flex: 3,
          child: Container(
            margin: const EdgeInsets.only(right: 8),
            child: TextFormField(
              controller: controller,
              keyboardType: TextInputType.phone,
              autocorrect: false,
              textInputAction: TextInputAction.next,
              enableSuggestions: false,
              maxLength: 255,
              decoration: InputDecoration(
                labelText: t.phoneNumberInputTextLabel,
              ),
              validator: (value) => validate(t, value),
            ),
          ),
        ),
        Expanded(
          child: DropdownButton<PhoneNumberType>(
            isExpanded: true,
            value: type,
            items: PhoneNumberType.values
                .map(
                  (t) => DropdownMenuItem(
                    value: t,
                    child: Text(
                      t.translate(context),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                )
                .toList(),
            onChanged: (t) => setState(() => type = t!),
          ),
        ),
        IconButton(
          onPressed: () => widget.onRemoveClick(widget.key!),
          icon: const Icon(Icons.remove),
        )
      ],
    );
  }

  String get phoneNumber => controller.text.trim();

  String? validate(AppLocalizations t, String? value) {
    if (value == null) {
      return t.nullPhoneNumberError;
    } else if (value.trim().isEmpty) {
      return t.blankPhoneNumberError;
    } else if (widget.otherPhoneNumbers.any(
      (key) => key.currentState?.phoneNumber == value.trim(),
    )) {
      return t.duplicatePhoneNumberError;
    }

    return null;
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

class _ImageField extends StatelessWidget {
  final bool edit;

  const _ImageField({Key? key, required this.edit}) : super(key: key);

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<SettingsBloc, SettingsState>(
          builder: (context, settingsState) =>
              BlocConsumer<ContactsBloc, ContactsState>(
                listener: (context, state) =>
                    state.selectContactImage.error.foldLeft(null, (_, err) {
                  final t = AppLocalizations.of(context)!;
                  final message = err.whenOrNull(
                    permissionsToAccessStorageNotGranted: () =>
                        t.storagePermissionsError,
                    imageTooBig: () => t.imageTooBigError,
                  );
                  if (message != null) {
                    FlushbarHelper.createError(message: message).show(context);
                  }
                }),
                builder: (context, state) => state
                        .selectContactImage.isSelectingContactImage
                    ? const Center(child: CircularProgressIndicator())
                    : _getContactImage(state, edit).fold(
                        () => IconButton(
                          onPressed: () => onImageButtonClick(context),
                          icon: const Icon(Icons.add_photo_alternate_outlined),
                        ),
                        (img) => Center(
                          child: LayoutBuilder(
                            builder: (context, constraints) => ConstrainedBox(
                              child: InkWell(
                                child: Image.memory(img),
                                onTap: () => onImageButtonClick(context),
                              ),
                              constraints: BoxConstraints(
                                maxWidth: constraints.maxWidth / 3,
                              ),
                            ),
                          ),
                        ),
                      ),
              ));

  void onImageButtonClick(BuildContext context) {
    context.read<ContactsBloc>().selectImage(
          maxSize: context
              .read<SettingsBloc>()
              .state
              .settings
              .map((s) => s.maxImageSizeBytes)
              .toNullable(),
        );
  }
}

f.Option<Uint8List> _getContactImage(ContactsState state, bool edit) =>
    state.selectContactImage.contactImage.orElse(
      () => edit
          ? state.fetchSingleContact.contact.flatMap(
              (c) => f.optionOf(c.image),
            )
          : f.none(),
    );

class _FormButtons extends StatelessWidget {
  final VoidCallback onSubmit;
  final VoidCallback onCancel;

  const _FormButtons({
    Key? key,
    required this.onSubmit,
    required this.onCancel,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<ContactsBloc, ContactsState>(
        builder: (context, state) {
          final t = AppLocalizations.of(context)!;

          return Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              Flexible(
                child: ElevatedButton(
                  onPressed: onCancel,
                  child: Text(
                    t.cancelButtonText,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ),
              Flexible(
                child: Container(
                  margin: const EdgeInsets.only(left: 16),
                  child: isSubmitting(state)
                      ? const CircularProgressIndicator()
                      : ElevatedButton(
                          onPressed: onSubmit,
                          child: Text(
                            t.submitButtonText,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                ),
              )
            ],
          );
        },
      );

  bool isSubmitting(ContactsState state) =>
      state.createContact.isSubmittingContactCreation ||
      state.editContact.isSubmittingContactEdit;
}

class _PlusButton extends StatelessWidget {
  final VoidCallback onClick;
  const _PlusButton({Key? key, required this.onClick}) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        margin: const EdgeInsets.only(bottom: 50),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            IconButton(
              onPressed: onClick,
              icon: const Icon(Icons.add),
            ),
          ],
        ),
      );
}

class _GeneralInfoField extends StatefulWidget {
  final String? initialValue;
  final GlobalKey<_GeneralInfoFieldState>? otherKey;
  final _TranslateFunction label;
  final _TranslateFunction validationError;

  const _GeneralInfoField({
    this.initialValue,
    this.otherKey,
    required Key key,
    required this.label,
    required this.validationError,
  }) : super(key: key);

  @override
  State<_GeneralInfoField> createState() => _GeneralInfoFieldState();
}

class _GeneralInfoFieldState extends State<_GeneralInfoField> {
  late final TextEditingController controller;

  @override
  void initState() {
    controller = TextEditingController(text: widget.initialValue);
    super.initState();
  }

  @override
  Widget build(BuildContext context) => TextFormField(
        controller: controller,
        keyboardType: TextInputType.name,
        autocorrect: false,
        textInputAction: TextInputAction.next,
        enableSuggestions: false,
        maxLength: 255,
        decoration: InputDecoration(
          labelText: widget.label.call(AppLocalizations.of(context)!),
        ),
        validator: (value) => _validate(context, value),
      );

  String? _validate(BuildContext context, String? value) =>
      (value?.trim().isEmpty ?? true) &&
              (widget.otherKey?.currentState?.text.trim().isEmpty ?? true)
          ? widget.validationError.call(AppLocalizations.of(context)!)
          : null;

  String get text => controller.text.trim();

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

@freezed
class _KeyTextAndType<S extends State<StatefulWidget>, T>
    with _$_KeyTextAndType<S, T> {
  const factory _KeyTextAndType({
    required GlobalKey<S> key,
    String? text,
    T? type,
  }) = __KeyTextAndType<S, T>;
}
