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


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TextTokenizer {
    private final Analyzer analyzer;

    public TextTokenizer() {
        this(EnglishAnalyzer.getDefaultStopSet(), true);
    }

    public TextTokenizer(boolean filter) {
        this(EnglishAnalyzer.getDefaultStopSet(), filter);
    }

    public TextTokenizer(final CharArraySet stopWords, boolean withFilter) {
        if (withFilter) {
            analyzer = new BasicAnalyzer(stopWords);
        } else {
            analyzer = new BasicNoFilterAnalyzer();
        }
    }

    public String[] tokenize(String text) throws IOException {
        Iterable<String> tokens = split(text);

        ArrayList<String> elements = new ArrayList<>();
        for(String token: tokens) {
            elements.add(token);
        }

        String[] result = new String[elements.size()];
        return elements.toArray(result);
    }

    public Iterable<String> split(final Reader reader) throws IOException {
        final TokenStream tokenStream = analyzer.tokenStream("_garbage", reader);
        tokenStream.reset();

        return () -> new Iterator<>() {
            private String next = null;

            @Override
            public synchronized boolean hasNext() {
                try {
                    if (null == next) {
                        if (tokenStream.incrementToken()) {
                            next = tokenStream.getAttribute(CharTermAttribute.class).toString();
                            return true;
                        }
                    } else {
                        return true;
                    }
                    tokenStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public synchronized String next() {
                if (!hasNext()) throw new NoSuchElementException();

                String n = next;
                next = null;
                return n;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterable<String> split(final String text) throws IOException {
        return split(new StringReader(text));
    }
}
