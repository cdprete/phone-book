import 'dart:async';

import 'package:flutter/material.dart';
import 'package:phone_book/src/app.dart';
import 'package:phone_book/src/di/injector.dart';

void main() async {
  runZonedGuarded<Future<void>>(
    () async {
      await configureDependencies();
      runApp(MyApp());
    },
    (error, stack) => print('Uncaught ERROR: $error\n$stack'),
  );
}