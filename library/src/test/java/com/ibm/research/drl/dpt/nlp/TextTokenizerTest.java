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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

public class TextTokenizerTest {
    private final static String text1 = "Tony Blair obtained a “blessing” from Chinese leaders for a company owned by a Saudi prince to do business in China as part of an arrangement that paid the former UK prime minister’s firm £41,000 a month and a 2% commission on any multimillion-pound contracts he helped to secure.";
    private final static String text2 = "CCTV footage released on Thursday showed a vicious assault in which Owen briefly touches shoulders with a Thai man carrying a bottle who appears to push him to the ground. An argument ensues and the family are set upon, repeatedly punched and kicked in the face even as they lie on the ground.";

    @Test
    public void testEmails() throws IOException {
        TextTokenizer tt = new TextTokenizer();
        String text = "Hello, my email is foo@gmail.com and i am going to the shops!!";

        List<String> words = new ArrayList<>();

        for(String word: tt.split(text)) {
            words.add(word);
        }

        assertTrue(words.contains("foo@gmail.com"));
    }

    @Test
    public void splitReturnSomething() throws IOException {
        TextTokenizer tt = new TextTokenizer();
        List<String> words = new ArrayList<>();

        for (String word : tt.split(text1)) {
            words.add(word);
        }

        assertFalse(words.isEmpty());
        assertThat(words.size(), lessThan(text1.split(" ").length));
    }

    @Test
    public void splitReturnSomething2() throws IOException {
        TextTokenizer tt = new TextTokenizer();
        List<String> words = new ArrayList<>();

        for (String word : tt.split(text2)) {
            words.add(word);
        }

        assertFalse(words.isEmpty());
        assertThat(words.size(), lessThan(text2.split(" ").length));
    }

    @Test
    public void splitHandlesEmpty() throws IOException {
        TextTokenizer tt = new TextTokenizer();
        Iterator<String> iterator = tt.split("").iterator();

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testWithNoFilter() throws IOException {
        TextTokenizer noFilter = new TextTokenizer(false);
        assertNotNull(noFilter);

        Iterator<String> noFilterIterator = noFilter.split(text2).iterator();

        assertNotNull(noFilterIterator);

        List<String> noFilteredToken = new ArrayList<>();

        noFilterIterator.forEachRemaining(noFilteredToken::add);

        assertFalse(noFilteredToken.isEmpty());

        TextTokenizer withFilter = new TextTokenizer(true);
        assertNotNull(noFilter);
        Iterator<String> withFilterIterator = withFilter.split(text2).iterator();

        assertNotNull(withFilterIterator);

        List<String> withFilteredToken = new ArrayList<>();

        withFilterIterator.forEachRemaining(withFilteredToken::add);

        assertFalse(withFilteredToken.isEmpty());


        assertThat(noFilteredToken.size(), greaterThan(withFilteredToken.size()));
    }
}