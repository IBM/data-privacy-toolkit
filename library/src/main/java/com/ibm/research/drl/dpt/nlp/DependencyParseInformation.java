/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import java.util.Objects;



public final class DependencyParseInformation {
    private final String caseI;
    private final String nmod;
    private final String apposMod;

    public String getApposMod() {
        return apposMod;
    }

    public String getCase() {
        return caseI;
    }

    public String getNmod() {
        return nmod;
    }

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
