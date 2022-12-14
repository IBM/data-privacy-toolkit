/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class CustomIdentifierFactorySerializer extends StdSerializer<IdentifierFactory> {
    private static final Logger log = LogManager.getLogger(CustomIdentifierFactorySerializer.class);

    public CustomIdentifierFactorySerializer() {
        this(null);
    }

    public CustomIdentifierFactorySerializer(Class<IdentifierFactory> t) {
        super(t);
    }

    @Override
    public void serialize(
            IdentifierFactory identifierFactory, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeNull();
    }
}
