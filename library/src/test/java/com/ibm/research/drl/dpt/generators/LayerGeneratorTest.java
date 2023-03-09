/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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
