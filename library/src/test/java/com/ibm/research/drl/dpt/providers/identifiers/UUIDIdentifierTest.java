package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UUIDIdentifierTest {
    @Test
    public void UUIDIdentifierHappyPath() {
        Identifier identifier = new UUIDIdentifier();

        for (int i = 0; i < 100; i++) {
            String value = UUID.randomUUID().toString();

            assertThat(identifier.isOfThisType(value), is(true));
        }
    }

    @Test
    public void executeOnDataset() throws IOException {
        Identifier identifier = new UUIDIdentifier();

        try (InputStream data = getClass().getResourceAsStream("/test-uuid.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader((data)))
        ) {
            for (String value: reader.lines().collect(Collectors.toList())) {
                assertThat(identifier.isOfThisType(value), is(true));
            }
        }
    }
}