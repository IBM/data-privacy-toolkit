/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class CustomIdentifierFactoryDeserializer extends StdDeserializer<IdentifierFactory> {
    private static final Logger log = LogManager.getLogger(CustomIdentifierFactoryDeserializer.class);

    public CustomIdentifierFactoryDeserializer() {
        this(null);
    }

    public CustomIdentifierFactoryDeserializer(Class<IdentifierFactory> t) {
        super(t);
    }

    @Override
    public IdentifierFactory deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node != null) {
            if (!node.isNull()) {
                return IdentifierFactory.initializeIdentifiers(node);
            }
        }
        return null;
    }
}
