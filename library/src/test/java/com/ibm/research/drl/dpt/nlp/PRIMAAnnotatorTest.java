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
package com.ibm.research.drl.dpt.nlp;

import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PRIMAAnnotatorTest {

    @Test
    public void shingletsDoNotContainTrailingDot() throws Exception {
        try (InputStream configuration = PRIMAAnnotatorTest.class.getResourceAsStream("/PRIMAtestWithPhoneAndEmailOnly.json")) {
            PRIMAAnnotator annotator = new PRIMAAnnotator(
                    JsonUtils.MAPPER.readTree(configuration)
            );

            List<IdentifiedEntity> entities = annotator.identify(
                    "This is a nice day.\n" +
                            "Let's talk about something tomorrow.\n" +
                    "You can contact my at my number 877 218 1190.\n" +
                            "Or at my email, which is johnDoe@gmail.com. Have a nice day!", Language.ENGLISH);

            assertNotNull(entities);
            assertThat(entities.size(), is(2));
            assertThat(entities, containsInAnyOrder(
                    hasProperty("text", is("877 218 1190")),
                    hasProperty("text", is("johnDoe@gmail.com")))
            );
        }
    }

    @Test
    public void testIdentifierWithUserDefiendObjects() throws Exception {
        String text = "This is a sentence containing a name like John and also this weird thing: FOO_BAR123";

        try (InputStream configuration = PRIMAAnnotatorTest.class.getResourceAsStream("/PRIMAtestWithUserDefined.json")) {
            PRIMAAnnotator annotator = new PRIMAAnnotator(
                    JsonUtils.MAPPER.readTree(configuration)
            );

            List<IdentifiedEntity> entities = annotator.identify(text, Language.ENGLISH);

            assertNotNull(entities);
            assertThat(entities.size(), is(2));
        }
    }

    @Test
    public void testRealCases() throws Exception {
        String text = "[John] Can you try by using this link -https://ibm.webex.com/ibm/sc30/t.php?MTID=fdfafadsdfa";

        try (InputStream configuration = PRIMAAnnotatorTest.class.getResourceAsStream("/PRIMAtestNat.json")) {
            PRIMAAnnotator annotator = new PRIMAAnnotator(
                    JsonUtils.MAPPER.readTree(configuration)
            );

            List<IdentifiedEntity> entities = annotator.identify(text, Language.ENGLISH);

            assertNotNull(entities);
            assertThat(entities.size(), is(1));
            assertThat(entities, contains(
                    hasProperty("text", is("ibm/sc30/t.php?MTID=fdfafadsdfa")))
            );
        }
    }

    @Test
    public void testFromJP() throws Exception {
        String text = "[00023] The category discovery unit 116 first selects keywords 160 from each ontology file 114. Initially, initial classification may be bootstrapped from the lexical database 118. A sense filter 162 selects one or more senses for each selected keyword, which are relevant to the domain of the ontology file, by using the lexical database 118 to determine a significance value 164 for each selected keyword. The significance values 164 are a significance measure of senses from ontological synonym sets or synsets for each keyword that may be used to measure the context of each ontology file. A feature set 166 containing significant senses is defined for each ontology file and the feature set of each ontology file is normalized for comparison with other feature sets. The senses for each feature set are examined to select one sense (i.e., a domain or category) 168 that represents the corresponding ontology file as the ontology file category. Statistics are extracted for discovered domains 168 and used to select a number (e.g., a few dozen) of categories 170 representing the entire set of ontology files. Thus, although the directory service may start with a few dozen categories, the number of categories can grow as the directory service serves more and more ontology files. So, by hierarchically structuring the categories, better navigational support can be provides as that number grows.";

        try (InputStream configuration = PRIMAAnnotatorTest.class.getResourceAsStream("/prima_from_jp.json")) {
            PRIMAAnnotator annotator = new PRIMAAnnotator(
                    JsonUtils.MAPPER.readTree(configuration)
            );

            List<IdentifiedEntity> entities = annotator.identify(text, Language.ENGLISH);

            assertNotNull(entities);

            System.out.println(entities);
        }
    }
}