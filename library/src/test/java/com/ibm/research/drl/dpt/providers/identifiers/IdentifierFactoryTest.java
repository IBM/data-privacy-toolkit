/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IdentifierFactoryTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Disabled
    public void listTypes() {
        IdentifierFactory.getDefaultIdentifierFactory().availableIdentifiers().forEach(
                identifier -> System.out.println(identifier.getType().name())
        );
    }
    
    @Test
    @Disabled
    public void testSlowPerformance() throws Exception {
        String filename = "/Users/santonat/dev/cedp/lastupdatetime.csv";
        
        IdentifierFactory identifierFactory = IdentifierFactory.getDefaultIdentifierFactory();
       
        long total = 0;
        
        for(Identifier identifier: identifierFactory.availableIdentifiers()) {
            long tsStart = System.currentTimeMillis();
            int positive = 0;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (identifier.isOfThisType(line)) {
                         positive++;
                    }
                    
                }
            }
            
            long tsEnd = System.currentTimeMillis();
            System.out.println(identifier.getType() + " took " + (tsEnd - tsStart) + " ms : " + positive);
            total += (tsEnd - tsStart);
        }

        System.out.println("total : " + total);
    }

    @Test
    public void readDictionaryFromDirectory() {
        ArrayNode identifiers = mapper.createArrayNode();
        identifiers.add(
                mapper.createObjectNode().
                        put("type", PluggableIdentifierType.DICTIONARY.toString()).
                        put("providerType", "FOO").
                        put("ignoreCase", false).
                        set("paths", mapper.createArrayNode().add(
                                Objects.requireNonNull(this.getClass().getResource("/dict")).getPath()
                        ))
        );

        Collection<Identifier> available = IdentifierFactory.initializeIdentifiers(identifiers).availableIdentifiers();

        assertNotNull(available);
        assertThat(available.isEmpty(), is(false));
    }

    @Test
    public void readRegExesFromFile() {
        ArrayNode identifiers = mapper.createArrayNode();
        identifiers.add(
                mapper.createObjectNode().
                        put("type", PluggableIdentifierType.REGEX.toString()).
                        put("providerType", "FOO").
                        set("paths", mapper.createArrayNode().add(
                                this.getClass().getResource("/test_regex.txt").getPath()
                        ))
        );

         Collection<Identifier> available = IdentifierFactory.initializeIdentifiers(identifiers).availableIdentifiers();

        assertNotNull(available);
        assertThat(available.isEmpty(), is(false));
    }

    @Test
    public void testIdentifiersListWithUserDefinedRegexp() {
        ArrayNode node = mapper.createArrayNode();

        node
                .add(EmailIdentifier.class.getCanonicalName())
                .add(ZIPCodeIdentifier.class.getCanonicalName())
                .add(
                        mapper.createObjectNode()
                        .put("type", PluggableIdentifierType.REGEX.toString())
                        .put("providerType","FOO")
                        .set("regex", mapper.createArrayNode().add("FOO"))
                );

        IdentifierFactory factory = IdentifierFactory.initializeIdentifiers(node);

        assertNotNull(factory);

        Collection<Identifier> identifiers = factory.availableIdentifiers();

        assertNotNull(identifiers);
        assertThat(identifiers.size(), is(3));
        assertThat(identifiers.parallelStream().filter( identifier -> identifier instanceof EmailIdentifier).count(), is(1L));
        assertThat(identifiers.parallelStream().filter( identifier -> identifier instanceof ZIPCodeIdentifier).count(), is(1L));
        assertThat(identifiers.parallelStream().filter(i -> i.getType().getName().equals("FOO")).count(), is(1L));

    }
}
