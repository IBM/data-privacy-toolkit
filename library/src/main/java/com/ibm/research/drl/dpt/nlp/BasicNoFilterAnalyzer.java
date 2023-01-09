/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;



public final class BasicNoFilterAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String field) {
        Tokenizer source = new ClassicTokenizer();
        TokenStream filter = new StandardFilter(source);
        return new TokenStreamComponents(source, filter);
    }
}
