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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.ResourceBasedManager;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ItalianStopWordsIdentifier extends AbstractManagerBasedIdentifier {
    private final static Logger logger = LogManager.getLogger(ItalianStopWordsIdentifier.class);

    private final static StopWordsManager manager = new StopWordsManager();

    @Override
    protected Manager getManager() {
        return manager;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("STOP_WORDS");
    }

    @Override
    public String getDescription() {
        return "Stop words identifier for Italian";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    private static class StopWordsManager extends ResourceBasedManager<StopWord> {
        private List<StopWord> stopWords;

        @Override
        public Collection<StopWord> getItemList() {
            return stopWords;
        }

        @Override
        protected Collection<ResourceEntry> getResources() {
            return LocalizationManager.getInstance().getResources(Resource.STOP_WORDS);
        }

        @Override
        protected List<Tuple<String, StopWord>> parseResourceRecord(CSVRecord record, String countryCode) {
            if (null == stopWords) stopWords = new ArrayList<>();

            String word = record.get(0).strip().toUpperCase();

            StopWord stopWord = new StopWord(word, countryCode);

            stopWords.add(stopWord);

            return List.of(new Tuple<>(word, stopWord));
        }
    }

    private static class StopWord {
        private final String word;
        private final String countryCode;

        public StopWord(String word, String countryCode) {
            this.word = word;
            this.countryCode = countryCode;
        }
    }
}
