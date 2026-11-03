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

import java.util.Objects;



/** Holds dependency-parse information for an NLP token. */
public final class DependencyParseInformation {
    private final String caseI;
    private final String nmod;
    private final String apposMod;

    /**
     * Returns the appositive modifier.
     *
     * @return the appositional modifier string
     */
    public String getApposMod() {
        return apposMod;
    }

    /**
     * Returns the case dependency.
     *
     * @return the case string
     */
    public String getCase() {
        return caseI;
    }

    /**
     * Returns the nominal modifier.
     *
     * @return the nmod string
     */
    public String getNmod() {
        return nmod;
    }

    /**
     * Constructs a DependencyParseInformation.
     *
     * @param nmod      the nominal modifier
     * @param caseInfo  the case dependency string
     * @param apposMod  the appositional modifier
     */
    public DependencyParseInformation(String nmod, String caseInfo, String apposMod) {
       this.caseI = caseInfo;
       this.nmod = nmod;
       this.apposMod = apposMod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyParseInformation that = (DependencyParseInformation) o;
        return Objects.equals(caseI, that.caseI) &&
                Objects.equals(nmod, that.nmod) &&
                Objects.equals(apposMod, that.apposMod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseI, nmod, apposMod);
    }
}
