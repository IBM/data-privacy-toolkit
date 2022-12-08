/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Gender;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class NamesManagerTest {

    @Test
    public void getGenderForItalianNamesShouldWorkProperly() {
        NamesManager.Names names = NamesManager.instance();

        String maleName = "GIUFFRIDO";
        Gender maleGender = names.getGender(maleName);
        System.out.println(maleGender);
        assertThat(maleGender, is(Gender.male));

        String femaleName = "MARIALUIGIA";
        Gender femaleGender = names.getGender(femaleName);
        System.out.println(femaleGender);
        assertThat(femaleGender, is(Gender.female));
    }
}
