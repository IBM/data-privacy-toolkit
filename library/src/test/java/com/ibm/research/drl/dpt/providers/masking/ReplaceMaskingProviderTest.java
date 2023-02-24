/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReplaceMaskingProviderTest {

    @Test
    public void testPreserveBeginning() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_DETERMINISTIC");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 4);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "123-abc-xyz";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("123-", maskedValue.substring(0, 4));
        assertNotEquals("abc-xyz", maskedValue.substring(4));
    }

    @Test
    public void testPreserveInString() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_DETERMINISTIC");
        maskingConfiguration.setValue("replace.mask.offset", 3);
        maskingConfiguration.setValue("replace.mask.preserve", 5);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "123-abc-xyz";
        String maskedValue = maskingProvider.mask(originalValue);
        assertNotEquals("123", maskedValue.substring(0, 3));
        assertEquals("-abc-", maskedValue.substring(3, 8));
        assertNotEquals("xyz", maskedValue.substring(9));
    }

    @Test
    public void testWithPrefix() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_DETERMINISTIC");
        maskingConfiguration.setValue("replace.mask.prefix", "FOO-");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "567-abc-xyz";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("FOO-", maskedValue.substring(0, 4));
        assertNotEquals("abc-xyz", maskedValue.substring(5));
    }

    @Test
    public void testMaskIfInSet() {
        ArrayNode list = JsonUtils.MAPPER.createArrayNode();
        list.add("FOO");
        list.add("BAR");

        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        configuration.setValue("replace.mask.preserve", 0);
        configuration.setValue("replace.mask.replaceOnValueInSet", true);
        configuration.setValue("replace.mask.replaceOnValueNotInSet", false);
        configuration.setValue("replace.mask.testValues", list);

        MaskingProvider provider = new ReplaceMaskingProvider(configuration);

        assertThat(provider.mask("FOO"), not("FOO"));
        assertThat(provider.mask("BAR"), not("BAR"));
        assertThat(provider.mask("FOOBAR"), is("FOOBAR"));
    }

    @Test
    public void testMaskIfNotInSet() {
        ArrayNode list = JsonUtils.MAPPER.createArrayNode();
        list.add("FOO");
        list.add("BAR");

        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        configuration.setValue("replace.mask.preserve", 0);
        configuration.setValue("replace.mask.replaceOnValueInSet", false);
        configuration.setValue("replace.mask.replaceOnValueNotInSet", true);
        configuration.setValue("replace.mask.testValues", list);

        MaskingProvider provider = new ReplaceMaskingProvider(configuration);

        assertThat(provider.mask("FOO"), is("FOO"));
        assertThat(provider.mask("BAR"), is("BAR"));
        assertThat(provider.mask("FOOBAR"), not("FOOBAR"));
    }

    @Test
    public void testCompound() {
        ArrayNode valuesSet = JsonUtils.MAPPER.createArrayNode();
        valuesSet.add("FOO");
        valuesSet.add("BAR");

        ArrayNode testSet = JsonUtils.MAPPER.createArrayNode();
        testSet.add("XXXX");
        testSet.add("YYYY");

        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("replace.mask.mode", "WITH_SET");
        configuration.setValue("replace.mask.preserve", 0);
        configuration.setValue("replace.mask.replaceOnValueInSet", true);
        configuration.setValue("replace.mask.testValues", testSet);
        configuration.setValue("replace.mask.replacementValueSet", valuesSet);

        MaskingProvider provider = new ReplaceMaskingProvider(configuration);

        FieldRelationship relationship = new FieldRelationship(ValueClass.TEXT, RelationshipType.KEY, "foo", Collections.singletonList(new RelationshipOperand("bar")));
        Map<String, OriginalMaskedValuePair> values1 = Collections.singletonMap("bar", new OriginalMaskedValuePair("XXXX", "XXXX"));
        assertThat(provider.mask("asdf", "foo", relationship, values1), in(new String[]{"FOO", "BAR"}));

        Map<String, OriginalMaskedValuePair> values2 = Collections.singletonMap("bar", new OriginalMaskedValuePair("asfd", "asdf"));
        assertThat(provider.mask("asdf", "foo", relationship, values2), not(in(new String[]{"FOO", "BAR"})));

    }

    @Test
    public void testMaskReplacementFromSet() {
        ArrayNode list = JsonUtils.MAPPER.createArrayNode();
        list.add("FOO");
        list.add("BAR");

        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("replace.mask.mode", "WITH_SET");
        configuration.setValue("replace.mask.preserve", 0);
        configuration.setValue("replace.mask.replacementValueSet", list);

        MaskingProvider provider = new ReplaceMaskingProvider(configuration);

        assertThat(provider.mask("asdf"), in(new String[]{"FOO", "BAR"}));
        assertThat(provider.mask("dfasfa"), in(new String[]{"FOO", "BAR"}));
        assertThat(provider.mask(""), in(new String[]{"FOO", "BAR"}));
    }

    @Test
    public void testMask() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("replace.mask.preserve", 3);


        MaskingProvider maskingProvider = new ReplaceMaskingProvider(configuration);

        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("asd", maskedValue);
    }

    @Test
    public void testMaskEmptyValue() {
        MaskingProvider maskingProvider = new ReplaceMaskingProvider();
        String originalValue = "";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("", maskedValue);
    }
    
    @Test
    public void testMaskOffset() {
        //check offset
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.offset", 1);
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("sda", maskedValue);
    }

    @Test
    public void testMaskOffsetLargerThanDataLength() {
        //check when offset is greater than data length
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.offset", 10);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("", maskedValue);
    }

    @Test
    public void testRandom() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 2);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_RANDOM");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertNotEquals(maskedValue, originalValue);
        assertEquals("as", maskedValue.substring(0, 2));

        for(int i = 2; i < maskedValue.length(); i++) {
            assertTrue(Character.isLowerCase(maskedValue.charAt(i)));
        }
    }

    @Test
    public void testRandomMiddle() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.offset", 1);
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_RANDOM");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertNotEquals(maskedValue, originalValue);
        assertEquals("sda", maskedValue.substring(1, 4));

        assertTrue(Character.isLowerCase(maskedValue.charAt(0)));
        for(int i = 4; i < maskedValue.length(); i++) {
            assertTrue(Character.isLowerCase(maskedValue.charAt(i)));
        }
    }

    @Test
    public void testRandomEnd() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        maskingConfiguration.setValue("replace.mask.offset", 7);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_RANDOM");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);

        assertNotEquals(maskedValue, originalValue);
        assertEquals("d", maskedValue.substring(7, 8));

        for(int i = 0; i < 7; i++) {
            assertTrue(Character.isLowerCase(maskedValue.charAt(i)));
        }
    }

    @Test
    public void testRandomEndByOne() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.preserve", 1);
        maskingConfiguration.setValue("replace.mask.offset", 6);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_RANDOM");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);

        assertNotEquals(maskedValue, originalValue);
        assertEquals("a", maskedValue.substring(6, 7));

        for(int i = 0; i < 6; i++) {
            assertTrue(Character.isLowerCase(maskedValue.charAt(i)));
        }
        assertTrue(Character.isLowerCase(maskedValue.charAt(7)));
    }

    @Test
    public void testAsterisks() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 2);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("as******", maskedValue);
    }

    @Test
    public void testAsterisksMiddle() {
        //check the case we replace something in the middle
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        maskingConfiguration.setValue("replace.mask.offset", 1);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("*sda****", maskedValue);
    }

    @Test
    public void testAsterisksEnd() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        maskingConfiguration.setValue("replace.mask.offset", 7);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("*******d", maskedValue);
    }

    @Test
    public void testAsterisksEndByOne() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.preserve", 1);
        maskingConfiguration.setValue("replace.mask.offset", 6);
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("******a*", maskedValue);
    }

    @Test
    public void testMaskOverrun() {
        //check offset
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.offset", 7);
        maskingConfiguration.setValue("replace.mask.preserve", 4); // this is over the length of the string

        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("d", maskedValue);
    }

    @Test
    public void testWithSpecifiedAsterisks() {
            MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
            maskingConfiguration.setValue("replace.mask.preserve", 3);
            maskingConfiguration.setValue("replace.mask.offset", 7);
            maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
            maskingConfiguration.setValue("replace.mask.asteriskValue", "#");
            MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
            String originalValue = "asdasdad";
            String maskedValue = maskingProvider.mask(originalValue);
            assertEquals("#######d", maskedValue);

    }

    @Test
    public void testPartialWithPrefix() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        String originalValue = "asdasdad";

        maskingConfiguration.setValue("replace.mask.mode", "WITH_PARTIAL");
        maskingConfiguration.setValue("replace.mask.prefix", "PRF-");
        maskingConfiguration.setValue("replace.mask.offset", 1);
        maskingConfiguration.setValue("replace.mask.preserve", 3);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("PRF-sda", maskedValue);
    }

    @Test
    public void testRandomWithPrefix() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_RANDOM");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 2);
        maskingConfiguration.setValue("replace.mask.prefix", "PRF-");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertNotEquals(maskedValue, originalValue);
        assertEquals("PRF-as", maskedValue.substring(0, 6));

        for(int i = 4; i < maskedValue.length(); i++) {
            assertTrue(Character.isLowerCase(maskedValue.charAt(i)));
        }
    }

    @Test
    public void testAsterisksWithPrefix() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_ASTERISKS");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 2);
        maskingConfiguration.setValue("replace.mask.prefix", "PRF-");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("PRF-as******", maskedValue);
    }

    @Test
    public void testDeterministic() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_DETERMINISTIC");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 2);
        maskingConfiguration.setValue("replace.mask.prefix", "PRF-");
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "asdasdad";
        String maskedValue = maskingProvider.mask(originalValue);
        assertEquals("PRF-as", maskedValue.substring(0, 6));
    }

    @Test
    public void testI381DeterministicShouldBeReallyDeterministic() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("replace.mask.mode", "WITH_DETERMINISTIC");
        maskingConfiguration.setValue("replace.mask.offset", 0);
        maskingConfiguration.setValue("replace.mask.preserve", 4);
        MaskingProvider maskingProvider = new ReplaceMaskingProvider(maskingConfiguration);
        String originalValue = "123-abc-xyz";

        String maskedValue1 = maskingProvider.mask(originalValue);
        assertEquals("123-", maskedValue1.substring(0, 4));
        assertNotEquals("abc-xyz", maskedValue1.substring(4));

        String maskedValue2 = maskingProvider.mask(originalValue);
        assertEquals("123-", maskedValue2.substring(0, 4));
        assertNotEquals("abc-xyz", maskedValue2.substring(4));

        assertEquals(maskedValue1, maskedValue2);
    }
}
