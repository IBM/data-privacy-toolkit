package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.ValueClass;

import java.util.List;

public class UUIDIdentifier extends PluggableRegexIdentifier {
    public UUIDIdentifier() {
        super("UUID",
                List.of("GUID", "UUID", "UUIDv4"),  List.of("[0-9abcdef]{8}-[0-9abcdef]{4}-[0-9abcdef]{4}-[0-9abcdef]{4}-[0-9abcdef]{12}"), ValueClass.TEXT);
    }
}
