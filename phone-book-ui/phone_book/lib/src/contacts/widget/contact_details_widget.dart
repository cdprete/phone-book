import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:phone_book/src/api/contacts_api.dart';
import 'package:phone_book/src/contacts/bloc/contacts_bloc.dart';
import 'package:phone_book/src/contacts/view/delete_contact_view.dart';

class ContactDetailsWidget extends StatefulWidget {
  final Contact _contact;

  const ContactDetailsWidget({Key? key, required Contact contact})
      : _contact = contact,
        super(key: key);

  @override
  State<ContactDetailsWidget> createState() => _ContactDetailsWidgetState();
}

class _ContactDetailsWidgetState extends State<ContactDetailsWidget> {
  final ScrollController scrollController = ScrollController();

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<ContactsBloc, ContactsState>(
        builder: (context, state) {
          final t = AppLocalizations.of(context)!;

          return ListView(
            controller: scrollController,
            children: [
              _CategoryText(
                text: t.contactDetailsGeneralDetailsBlockLabel,
                first: true,
              ),
              if (widget._contact.image != null)
                Center(
                  child: LayoutBuilder(
                    builder: (context, constraints) => ConstrainedBox(
                      child: Image.memory(widget._contact.image!),
                      constraints: BoxConstraints(
                        maxWidth: constraints.maxWidth / 3,
                      ),
                    ),
                  ),
                ),
              _SingleTextField(
                fieldName: t.contactDetailsNameFieldName,
                fieldValue: widget._contact.name,
              ),
              _SingleTextField(
                fieldName: t.contactDetailsSurnameFieldName,
                fieldValue: widget._contact.surname,
              ),
              _CategoryText(text: t.contactDetailsEmailAddressesBlockLabel),
              for (EmailAddress e in widget._contact.emailAddresses)
                _TextRowField(
                  values: [e.emailAddress, e.type.translate(context)],
                ),
              _CategoryText(text: t.contactDetailsPhoneNumbersBlockLabel),
              for (PhoneNumber p in widget._contact.phoneNumbers)
                _TextRowField(
                  values: [p.phoneNumber, p.type.translate(context)],
                ),
              _ButtonsRow(contact: widget._contact),
            ],
          );
        },
      );

  @override
  void dispose() {
    scrollController.dispose();
    super.dispose();
  }
}

class _ButtonsRow extends StatelessWidget {
  final Contact contact;

  const _ButtonsRow({Key? key, required this.contact}) : super(key: key);

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<ContactsBloc, ContactsState>(
        builder: (context, state) {
          final t = AppLocalizations.of(context)!;

          return Container(
            margin: const EdgeInsets.only(top: 50),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Container(
                  margin: const EdgeInsets.only(right: 16),
                  child: _DeleteButton(contact: contact),
                ),
                ElevatedButton(
                  onPressed: () => onEditContact(context),
                  child: Text(t.editButtonText),
                ),
              ],
            ),
          );
        },
      );

  void onEditContact(BuildContext context) =>
      context.read<ContactsBloc>().startEditContact(contact);
}

class _DeleteButton extends StatelessWidget {
  final Contact contact;

  const _DeleteButton({Key? key, required this.contact}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<ContactsBloc, ContactsState>(
      builder: (context, state) => state.deleteContact.isDeletingContact
          ? const CircularProgressIndicator()
          : ElevatedButton(
              onPressed: () => onDeleteContact(context),
              child: Text(AppLocalizations.of(context)!.deleteButtonText),
            ),
    );
  }

  void onDeleteContact(BuildContext context) async {
    final toBeDeleted = (await showDialog<bool>(
          context: context,
          builder: (context) => const DeleteContactView(),
        )) ??
        false;
    if (toBeDeleted) {
      context.read<ContactsBloc>().deleteContact(contact);
    }
  }
}

class _CategoryText extends StatelessWidget {
  final bool first;
  final String text;

  const _CategoryText({
    Key? key,
    this.first = false,
    required this.text,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        margin: first
            ? const EdgeInsets.only(bottom: 8)
            : const EdgeInsets.symmetric(vertical: 8),
        child: Text(
          text,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
        ),
      );
}

class _TextRowField extends StatelessWidget {
  final List<String> values;

  const _TextRowField({Key? key, required this.values}) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        margin: const EdgeInsets.only(left: 16, top: 8),
        child: Row(
          children: values.map((v) => Expanded(child: Text(v))).toList(),
        ),
      );
}

class _SingleTextField extends StatelessWidget {
  final String fieldName;
  final String? fieldValue;

  const _SingleTextField({
    Key? key,
    required this.fieldName,
    required this.fieldValue,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        margin: const EdgeInsets.only(left: 16, top: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              fieldName,
              style: TextStyle(color: Colors.grey.shade500, fontSize: 10),
            ),
            Text(fieldValue ?? '')
          ],
        ),
      );
}
