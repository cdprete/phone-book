import 'package:flutter/material.dart';
import 'package:phone_book/localization/app_localizations.dart';

class NoContactSelectedWidget extends StatelessWidget {
  const NoContactSelectedWidget({super.key});

  @override
  Widget build(BuildContext context) => Center(
    child: Text(
      AppLocalizations.of(context)!.noContactSelectedText,
      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
      overflow: TextOverflow.ellipsis,
    ),
  );
}
