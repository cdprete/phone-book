import 'dart:ui';

import 'package:another_flushbar/flushbar_helper.dart';
import 'package:flutter/material.dart' hide Page;
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import 'package:phone_book/src/api/contacts_api.dart';
import 'package:phone_book/src/contacts/bloc/contacts_bloc.dart';
import 'package:phone_book/src/contacts/widget/contact_item_widget.dart';
import 'package:phone_book/src/contacts/widget/no_contacts_widget.dart';

class ContactsWidget extends StatefulWidget {
  final ContactItemClickCallback _onContactClick;

  const ContactsWidget({
    Key? key,
    required ContactItemClickCallback onContactClick,
  })  : _onContactClick = onContactClick,
        super(key: key);

  @override
  State<ContactsWidget> createState() => _ContactsWidgetState();
}

class _ContactsWidgetState extends State<ContactsWidget> {
  final PagingController<int, Contact> pagingController =
      PagingController(firstPageKey: Page.firstPageNumber);

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<ContactsBloc, ContactsState>(
      listenWhen: (prev, curr) {
        bool create = curr.createContact.createContactSuccess;
        bool edit = curr.editContact.editContactSuccess;
        bool delete = curr.deleteContact.deleteContactSuccess;
        bool hasFetchedPage = prev.fetchContacts.isFetchingContacts &&
            !curr.fetchContacts.isFetchingContacts &&
            curr.fetchContacts.error.isNone();
        bool isPageChanged = prev.fetchContacts.contactsPage.currentPage !=
            curr.fetchContacts.contactsPage.currentPage;

        return create || edit || delete || hasFetchedPage || isPageChanged;
      },
      listener: (context, state) {
        final t = AppLocalizations.of(context)!;

        bool create = state.createContact.createContactSuccess;
        bool edit = state.editContact.editContactSuccess;
        bool delete = state.deleteContact.deleteContactSuccess;
        if (create || edit || delete) {
          final String message;
          if (create) {
            message = t.createContactSuccessfulMessage;
          } else if (edit) {
            message = t.editContactSuccessfulMessage;
          } else {
            message = t.deleteContactSuccessfulMessage;
          }
          FlushbarHelper.createSuccess(
            message: message,
          ).show(context);
          pagingController.refresh();
        } else {
          _addFetchedContacts(state);
        }
      },
      builder: (context, state) => RefreshIndicator(
        onRefresh: () => Future.sync(pagingController.refresh),
        child: ScrollConfiguration(
          behavior: ScrollConfiguration.of(context).copyWith(dragDevices: {
            PointerDeviceKind.touch,
            PointerDeviceKind.mouse,
          }),
          child: PagedListView<int, Contact>.separated(
            pagingController: pagingController,
            separatorBuilder: (context, index) => const SizedBox(height: 8),
            builderDelegate: PagedChildBuilderDelegate<Contact>(
              noItemsFoundIndicatorBuilder: (context) =>
                  const NoContactsWidget(),
              itemBuilder: (context, item, index) => ContactItemWidget(
                contact: item,
                onClick: widget._onContactClick,
              ),
            ),
          ),
        ),
      ),
    );
  }

  void _addFetchedContacts(ContactsState state) {
    final fetchState = state.fetchContacts;
    if (!fetchState.isFetchingContacts) {
      fetchState.error.fold(() {
        final items = fetchState.contactsPage.items.toList();
        if (fetchState.contactsPage.hasNextPage) {
          pagingController.appendPage(
            items,
            fetchState.contactsPage.currentPage + 1,
          );
        } else {
          pagingController.appendLastPage(items);
        }
      }, (err) => pagingController.error = err);
    }
  }

  @override
  void initState() {
    pagingController.addPageRequestListener(
      (page) => context.read<ContactsBloc>().fetchContacts(page: page),
    );
    pagingController.addStatusListener((status) {
      if ((status == PagingStatus.firstPageError ||
              status == PagingStatus.subsequentPageError) &&
          pagingController.error != null) {
        final err = (pagingController.error as ContactsError).whenOrNull(
            unauthenticated: () =>
                AppLocalizations.of(context)!.fetchContactsUnauthenticatedError,
            unexpectedError: (err, _) => err);
        if (err != null) {
          FlushbarHelper.createError(message: err).show(context);
        }
      }
    });
    super.initState();
  }

  @override
  void dispose() {
    pagingController.dispose();
    super.dispose();
  }
}
