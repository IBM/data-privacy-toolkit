package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.ValueClass;

import java.util.List;

/** Identifier for UUID / GUID values (version 4). */
public class UUIDIdentifier extends PluggableRegexIdentifier {
    /** Constructs a UUIDIdentifier with the standard UUID v4 pattern. */
    public UUIDIdentifier() {
        super("UUID",
                List.of("GUID", "UUID", "UUIDv4"), List.of("[0-9abcdef]{8}-[0-9abcdef]{4}-[0-9abcdef]{4}-[0-9abcdef]{4}-[0-9abcdef]{12}"), ValueClass.TEXT);
    }
}
