/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

public class TextTokenizerTest {
    private final static String text1 = "Tony Blair obtained a “blessing” from Chinese leaders for a company owned by a Saudi prince to do business in China as part of an arrangement that paid the former UK prime minister’s firm £41,000 a month and a 2% commission on any multimillion-pound contracts he helped to secure.";
    private final static String text2 = "CCTV footage released on Thursday showed a vicious assault in which Owen briefly touches shoulders with a Thai man carrying a bottle who appears to push him to the ground. An argument ensues and the family are set upon, repeatedly punched and kicked in the face even as they lie on the ground.";
    private TextTokenizer tt;

    @BeforeEach
    public void setUp() throws Exception {
        tt = new TextTokenizer();

        assertNotNull(tt);
    }

    @Test
    public void testEmails() throws IOException {
        String text = "Hello, my email is foo@gmail.com and i am going to the shops!!";

        List<String> words = new ArrayList<>();

        for(String word: tt.split(text)) {
            words.add(word);
        }

        assertTrue(words.contains("foo@gmail.com"));
    }

    @Test
    public void splitReturnSomething() throws IOException {
        List<String> words = new ArrayList<>();

        for (String word : tt.split(text1)) {
            words.add(word);
        }

        assertFalse(words.isEmpty());
        assertThat(words.size(), lessThan(text1.split(" ").length));
    }

    @Test
    public void splitReturnSomething2() throws IOException {
        List<String> words = new ArrayList<>();

        for (String word : tt.split(text2)) {
            words.add(word);
        }

        assertFalse(words.isEmpty());
        assertThat(words.size(), lessThan(text2.split(" ").length));
    }

    @Test
    public void splitHandlesEmpty() throws IOException {
        Iterator<String> iterator = tt.split("").iterator();

        assertFalse(iterator.hasNext());
    }
}