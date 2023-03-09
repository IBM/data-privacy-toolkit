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
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.FirstName;
import com.ibm.research.drl.dpt.models.Gender;
import com.ibm.research.drl.dpt.models.LastName;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NamesManager implements Serializable {
    private final static Logger log = LogManager.getLogger(NamesManager.class);

    private final static class LastNameManager extends ResourceBasedManager<LastName> implements Serializable {

        @Override
        protected Collection<ResourceEntry> getResources() {
            return LocalizationManager.getInstance().getResources(Resource.LAST_NAME);
        }

        @Override
        protected List<Tuple<String, LastName>> parseResourceRecord(CSVRecord line, String countryCode) {
            String name = line.get(0);
            String key = name.toUpperCase();

            LastName lastName = new LastName(name, countryCode);

            return List.of(new Tuple<>(key, lastName));
        }

        @Override
        public Collection<LastName> getItemList() {
            return getValues();
        }
    }

    private final static class MaleNameManager extends ResourceBasedManager<FirstName> implements Serializable {

        @Override
        protected Collection<ResourceEntry> getResources() {
            return LocalizationManager.getInstance().getResources(Resource.FIRST_NAME_MALE);
        }

        @Override
        protected List<Tuple<String, FirstName>> parseResourceRecord(CSVRecord line, String countryCode) {
            String name = line.get(0);
            String key = line.get(0).toUpperCase();

            FirstName firstName = new FirstName(name, countryCode, Gender.male);

            return List.of(new Tuple<>(key, firstName));
        }

        @Override
        public Collection<FirstName> getItemList() {
            return getValues();
        }
    }

    private final static class FemaleNameManager extends ResourceBasedManager<FirstName> implements Serializable {

        @Override
        protected Collection<ResourceEntry> getResources() {
            return LocalizationManager.getInstance().getResources(Resource.FIRST_NAME_FEMALE);
        }

        @Override
        protected List<Tuple<String, FirstName>> parseResourceRecord(CSVRecord line, String countryCode) {
            String name = line.get(0);
            String key = line.get(0).toUpperCase();

            FirstName firstName = new FirstName(name, countryCode, Gender.female);

            return List.of(new Tuple<>(key, firstName));
        }

        @Override
        public Collection<FirstName> getItemList() {
            return getValues();
        }
    }


    /**
     * The type Names.
     */
    public final static class Names implements Serializable {

        private static final LastNameManager lastNameManager = new LastNameManager();
        private static final MaleNameManager maleNameManager = new MaleNameManager();
        private static final FemaleNameManager femaleNameManager = new FemaleNameManager();

        private final SecureRandom random;

        private Names() {
            this.random = new SecureRandom();
        }

        /**
         * Is last name boolean.
         *
         * @param candidate the candidate
         * @return the boolean
         */
        public boolean isLastName(String candidate) {
            return lastNameManager.isValidKey(candidate);
        }

        /**
         * Is first name boolean.
         *
         * @param candidate the candidate
         * @return the boolean
         */
        public boolean isFirstName(String candidate) {
            return maleNameManager.isValidKey(candidate) || femaleNameManager.isValidKey(candidate);
        }

        /**
         * Gets last name.
         *
         * @param identifier the identifier
         * @return the last name
         */
        public LastName getLastName(String identifier) {
            return lastNameManager.getKey(identifier);
        }

        /**
         * Gets first name.
         *
         * @param identifier the identifier
         * @return the first name
         */
        public FirstName getFirstName(String identifier) {
            FirstName firstName = maleNameManager.getKey(identifier);

            if (firstName != null) {
                return firstName;
            }

            return femaleNameManager.getKey(identifier);
        }

        /**
         * Gets last name.
         *
         * @return the last name
         */
        public String getRandomLastName() {
            return lastNameManager.getRandomKey();
        }

        /**
         * Gets random last name.
         *
         * @param countryCode the country code
         * @return the random last name
         */
        public String getRandomLastName(String countryCode) {
            return lastNameManager.getRandomKey(countryCode);
        }

        /**
         * Gets first name.
         *
         * @return the first name
         */
        public String getRandomFirstName() {
            boolean coin = random.nextBoolean();

            if (coin) {
                return maleNameManager.getRandomKey();
            } else {
                return femaleNameManager.getRandomKey();
            }
        }

        private String getRandomFirstName(Gender gender, String countryCode) {
            if (gender == Gender.male) {
                return maleNameManager.getRandomKey(countryCode);
            } else {
                return femaleNameManager.getRandomKey(countryCode);
            }
        }

        /**
         * Gets first name.
         *
         * @param gender         the gender
         * @param allowAnyGender the allow unisex
         * @param countryCode    the country code
         * @return the first name
         */
        public String getRandomFirstName(Gender gender, boolean allowAnyGender, String countryCode) {

            String name = null;

            int currentTry = 0;
            for (; currentTry < 50; ++currentTry) {
                name = getRandomFirstName(gender, countryCode);
                Gender newGender = getGender(name);

                if (allowAnyGender ||
                        newGender.equals(gender) ||
                        newGender.equals(Gender.both)) {
                    break;
                }
            }

            log.debug("First name identified after {}", currentTry);
            return name;
        }

        /**
         * Gets gender.
         *
         * @param candidate the candidate
         * @return the gender
         */
        public Gender getGender(String candidate) {
            boolean isMale = maleNameManager.isValidKey(candidate);
            boolean isFemale = femaleNameManager.isValidKey(candidate);

            if (isMale && isFemale) {
                return Gender.both;
            }

            if (isMale) {
                return Gender.male;
            } else {
                return Gender.female;
            }
        }

        public String getPseudoRandomFirstName(Gender gender, boolean allowAnyGender, String identifier) {
            if (allowAnyGender || Gender.both == gender) {
                if (0 == identifier.hashCode() % 2) {
                    return maleNameManager.getPseudorandom(identifier);
                } else {
                    return femaleNameManager.getPseudorandom(identifier);
                }
            } else if (Gender.male == gender) {
                return maleNameManager.getPseudorandom(identifier);
            } else {
                // assuming female
                return femaleNameManager.getPseudorandom(identifier);
            }
        }

        public String getPseudoRandomLastName(String identifier) {
            return lastNameManager.getPseudorandom(identifier);
        }
    }

    private static final Names instance = new Names();

    /**
     * Instance names.
     *
     * @return the names
     */
    public static Names instance() {
        return instance;
    }
}
