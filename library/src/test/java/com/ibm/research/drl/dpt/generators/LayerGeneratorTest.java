/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static java.time.Duration.ofMillis;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class LayerGeneratorTest {

    @Test
    public void testGenerateAll4() {
        LayerGenerator generator = new LayerGenerator(4);

        Collection<ItemSet> generated = new ArrayList<>();

        while (generator.hasNext()) {
            ItemSet itemSet = generator.next();
            generated.add(itemSet);
        }

        assertThat(generated.size(), is(16 - 1));
    }

    @Test
    public void testGenerateAll() {
        LayerGenerator generator = new LayerGenerator(5);

        Collection<ItemSet> generated = new ArrayList<>();

        while (generator.hasNext()) {
            generated.add(generator.next());

        }

        assertThat(generated.size(), is(32 - 1));
    }

    @Disabled("because depends on the load of Travis")
    @Test
    public void decentSize() {
        assertTimeout(ofMillis(30L), () -> {
            LayerGenerator generator = new LayerGenerator(10);

            int counter = 0;
            while (generator.hasNext()) {
                generator.next();
                ++counter;
            }

            assertThat(counter, is((int) (Math.pow(2, 10) - 1)));
        });
    }

    @Test
    public void testBan() {
        LayerGenerator generator = new LayerGenerator(5);
        generator.ban(new ItemSet(0));

        Collection<ItemSet> generated = new ArrayList<>();

        while (generator.hasNext()) {
            generated.add(generator.next());
        }

        assertThat(generated.size(), is(32 / 2 - 1));
    }
}
