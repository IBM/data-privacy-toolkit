package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DependencyParseInformationTest {
    @Test
    public void  basicTesting() {
        DependencyParseInformation dpi = new DependencyParseInformation("FOO", "BAR", "OPTION");

        assertNotNull(dpi);

        assertThat(dpi.getCase(), is("BAR"));
        assertThat(dpi.getNmod(), is("FOO"));
        assertThat(dpi.getApposMod(), is("OPTION"));

        assertEquals(dpi, dpi);

        DependencyParseInformation dpi2 = new DependencyParseInformation("FOO", "BAR", "OPTION");

        assertEquals(dpi, dpi2);

        DependencyParseInformation dpi3 = new DependencyParseInformation("FOO1", "BAR1", "OPTION1");

        assertNotEquals(dpi, dpi3);
    }
}