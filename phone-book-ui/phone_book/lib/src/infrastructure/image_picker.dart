import 'dart:async';
import 'dart:typed_data';

import 'package:dartz/dartz.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/services.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'image_picker.freezed.dart';

const String _accessToStorageNotGrantedErrorCode =
    "read_external_storage_denied";

@freezed
class ImagePickerError with _$ImagePickerError {
  const factory ImagePickerError.permissionsToAccessStorageNotGranted() =
      PermissionsToAccessStorageNotGranted;
  const factory ImagePickerError.imageTooBig() = ImageTooBig;
  const factory ImagePickerError.unexpectedError({
    String? message,
    StackTrace? stacktrace,
  }) = ImagePickerUnexpectedError;
}

FutureOr<Either<ImagePickerError, Uint8List?>> pickImage([int? maxSize]) async {
  try {
    final data = (await FilePicker.platform
            .pickFiles(type: FileType.image, withData: true))
        ?.files
        .single;
    return data == null || maxSize == null
        ? right(data?.bytes)
        : data.size <= maxSize
            ? right(data.bytes)
            : left(const ImagePickerError.imageTooBig());
  } on PlatformException catch (ex, stacktrace) {
    if (ex.code == _accessToStorageNotGrantedErrorCode) {
      return left(
        const ImagePickerError.permissionsToAccessStorageNotGranted(),
      );
    } else {
      return left(ImagePickerError.unexpectedError(
        message: ex.toString(),
        stacktrace: stacktrace,
      ));
    }
  }
}
