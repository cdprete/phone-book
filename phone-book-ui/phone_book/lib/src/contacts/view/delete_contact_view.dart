import 'package:flutter/material.dart';
import 'package:phone_book/localization/app_localizations.dart';

class DeleteContactView extends StatelessWidget {
  const DeleteContactView({super.key});

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;

    return AlertDialog(
      title: Text(t.deleteContactDialogText),
      content: Text(t.deleteContactDialogText),
      actions: [
        ElevatedButton(
          onPressed: () => Navigator.pop(context, false),
          child: Text(t.noButtonText),
        ),
        ElevatedButton(
          onPressed: () => Navigator.pop(context, true),
          child: Text(t.yesButtonText),
        ),
      ],
    );
  }
}
