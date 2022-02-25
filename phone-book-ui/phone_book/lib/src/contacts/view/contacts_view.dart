import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:phone_book/src/contacts/bloc/contacts_bloc.dart';
import 'package:phone_book/src/contacts/widget/contact_details_widget.dart';
import 'package:phone_book/src/contacts/widget/contacts_widget.dart';
import 'package:phone_book/src/contacts/widget/edit_contact_widget.dart';
import 'package:phone_book/src/contacts/widget/no_contact_selected_widget.dart';

class MasterDetailContactsView extends StatefulWidget {
  const MasterDetailContactsView({Key? key}) : super(key: key);

  @override
  State<MasterDetailContactsView> createState() =>
      _MasterDetailContactsViewState();
}

class _MasterDetailContactsViewState extends State<MasterDetailContactsView> {
  final GlobalKey addButtonKey = GlobalKey();

  double addButtonHeight = 0;

  @override
  void initState() {
    WidgetsBinding.instance?.addPostFrameCallback(
      (_) => setState(
        () => addButtonHeight = addButtonKey.currentContext?.size?.height ?? 0,
      ),
    );
    super.initState();
  }

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<ContactsBloc, ContactsState>(
        builder: (context, state) => Scaffold(
          appBar: AppBar(title: Text(AppLocalizations.of(context)!.appTitle)),
          body: Container(
            margin: const EdgeInsets.all(16),
            child: Row(
              children: [
                Expanded(
                  child: Container(
                    margin: const EdgeInsets.only(right: 16),
                    child: ContactsWidget(
                      onContactClick: (id) => fetchContact(context, id),
                    ),
                  ),
                ),
                Expanded(
                  child: _DetailView(addButtonHeight: addButtonHeight),
                  flex: 4,
                )
              ],
            ),
          ),
          floatingActionButton: canShowAddButton(state)
              ? FloatingActionButton(
                  child: const Icon(Icons.add),
                  onPressed: () => onCreateButtonClick(context),
                )
              : null,
        ),
      );

  void fetchContact(BuildContext context, String id) {
    context.read<ContactsBloc>().fetchContact(id);
  }

  bool canShowAddButton(ContactsState state) =>
      !state.createContact.isCreatingContact &&
      !state.editContact.isEditingContact;

  void onCreateButtonClick(BuildContext context) =>
      context.read<ContactsBloc>().startCreateContact();
}

class _DetailView extends StatelessWidget {
  final double addButtonHeight;

  const _DetailView({
    Key? key,
    required this.addButtonHeight,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) =>
      BlocBuilder<ContactsBloc, ContactsState>(
        builder: (context, state) {
          Widget toReturn;
          final selectedContact = state.fetchSingleContact.contact.toNullable();
          if (state.createContact.isCreatingContact) {
            toReturn = const EditContactWidget(edit: false);
          } else if (state.editContact.isEditingContact) {
            toReturn = const EditContactWidget();
          } else if (state.fetchContacts.isFetchingContacts) {
            toReturn = const Center(child: CircularProgressIndicator());
          } else if (selectedContact == null) {
            toReturn = const NoContactSelectedWidget();
          } else {
            toReturn = Container(
              margin: EdgeInsets.only(bottom: addButtonHeight + 15),
              child: ContactDetailsWidget(contact: selectedContact),
            );
          }

          return toReturn;
        },
      );
}
