import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:phone_book/src/contacts/bloc/contacts_bloc.dart';
import 'package:phone_book/src/contacts/view/contacts_view.dart';
import 'package:phone_book/src/di/injector.dart';

class ContactsPage extends StatelessWidget {
  const ContactsPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider<ContactsBloc>(
      create: (_) => getIt.get(),
      child: const SafeArea(child: MasterDetailContactsView()),
    );
  }
}
