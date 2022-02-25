import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class NoContactSelectedWidget extends StatelessWidget {
  const NoContactSelectedWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) => Center(
        child: Text(
          AppLocalizations.of(context)!.noContactSelectedText,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          overflow: TextOverflow.ellipsis,
        ),
      );
}
