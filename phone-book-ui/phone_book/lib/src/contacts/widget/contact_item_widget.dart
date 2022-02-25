import 'dart:math';

import 'package:flutter/material.dart';
import 'package:phone_book/src/api/contacts_api.dart';

typedef ContactItemClickCallback = void Function(String id);

class ContactItemWidget extends StatelessWidget {
  final Contact _contact;
  final ContactItemClickCallback _onClick;

  const ContactItemWidget({
    Key? key,
    required Contact contact,
    required ContactItemClickCallback onClick,
  })  : _contact = contact,
        _onClick = onClick,
        super(key: key);

  @override
  Widget build(BuildContext context) => Material(
        child: InkWell(
          onTap: () => _onClick.call(_contact.id!),
          child: Row(
            children: [
              Flexible(
                child: Container(
                  margin: const EdgeInsets.only(right: 8),
                  child: _contact.image == null
                      ? CircleAvatar(
                          backgroundColor: _getRandomColor(),
                          child: Text(
                            _contact.initials,
                            style: const TextStyle(color: Colors.white),
                          ),
                        )
                      : CircleAvatar(
                          backgroundImage: MemoryImage(_contact.image!),
                        ),
                ),
              ),
              Flexible(
                child: Text(
                  _contact.fullName,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                  overflow: TextOverflow.ellipsis,
                ),
              )
            ],
          ),
        ),
      );

  static Color _getRandomColor() =>
      Colors.primaries[Random().nextInt(Colors.primaries.length)];
}
