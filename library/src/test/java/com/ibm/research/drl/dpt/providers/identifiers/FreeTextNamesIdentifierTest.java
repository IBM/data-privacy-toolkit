/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FreeTextNamesIdentifierTest {
  
    @Test
    @Disabled
    public void testQuick() {
        String input = ", M.D.";
        IdentifierWithOffset nameIdentifier = new FreeTextNamesIdentifier();
        
        Tuple<Boolean, Tuple<Integer, Integer>> result = nameIdentifier.isOfThisTypeWithOffset(input);
        System.out.println(result.getFirst());
        if (result.getFirst()) {
            System.out.println(result.getSecond().getFirst() + ":" + result.getSecond().getSecond());
        }
    }
   
    @Test
    @Disabled
    public void testMultiLine() {
        String input = "John Doe MD\n" +
        "Cool Office\n" + 
        "Company Rocks Medical Associates";
        
        IdentifierWithOffset nameIdentifier = new FreeTextNamesIdentifier();

        Tuple<Boolean, Tuple<Integer, Integer>> result = nameIdentifier.isOfThisTypeWithOffset(input);
        System.out.println(result.getFirst());
    }
    
    @Test
    public void testWithOffsets() {
        IdentifierWithOffset nameIdentifier = new FreeTextNamesIdentifier();

        List<Tuple<String, Tuple<Boolean, Tuple<Integer, Integer>>>> expectedResults = Arrays.asList(
                new Tuple<>("  Dr. Leathers", new Tuple<>(true, new Tuple<>(6, "  Dr. Leathers".length() - 6))),
                
                new Tuple<>(", John Doe MD", new Tuple<>(true, new Tuple<>(2, "John Doe MD".length() - 3))),
                new Tuple<>("Ms. Nicholson:", new Tuple<>(true, new Tuple<>(4, "Ms. Nicholson:".length() - 4 - 1))),
                new Tuple<>("C/D OD .", new Tuple<>(false, null)),
                new Tuple<>("Lids/Lashes", new Tuple<>(false, null)),
                new Tuple<>("Kress. Capital", new Tuple<>(false, null)),
                new Tuple<>("RN Jack Johnson", new Tuple<>(false, null)),
                //new Tuple<>("James Doe, RN    Mon Aug", new Tuple<>(true, new Tuple<>(0, "James Doe, RN".length() - 1))),

                new Tuple<>("Buffet Warren Sin, M.D.", new Tuple<>(true, new Tuple<>(0, "Wonsuk Warren Suh, M.D.".length() - 6))),
                new Tuple<>("FLOWERS JR. MD, H. EUGENE", new Tuple<>(true, new Tuple<>(0, "LINDSEY JR. MD, H. EUGENE".length()))),
                new Tuple<>("DR. Leathers", new Tuple<>(true, new Tuple<>(4, "DR. Leathers".length() - 4))),
                new Tuple<>("DR. LEATHERS", new Tuple<>(true, new Tuple<>(4, "DR. LEATHERS".length() - 4))),
                new Tuple<>("Dr. LEATHERS", new Tuple<>(true, new Tuple<>(4, "Dr. LEATHERS".length() - 4))),

                
                new Tuple<>("Dr. Leathers", new Tuple<>(true, new Tuple<>(4, "Dr. Leathers".length() - 4))),
                new Tuple<>("Dr. Tovey,", new Tuple<>(true, new Tuple<>(4, 5))), //do not consider trailing comma
                new Tuple<>("Dr. Tovey, ", new Tuple<>(true, new Tuple<>(4, 5))), //do not consider trailing comma
                new Tuple<>("Clinician Name: John Doe, MD", new Tuple<>(false, null)),
                new Tuple<>("Dr.", new Tuple<>(false, null)),

                new Tuple<>("Dr. O'Brien", new Tuple<>(true, new Tuple<>(4, "Dr. O'Brien".length() - 4))), //this is valid
                new Tuple<>("Dr. OBrien", new Tuple<>(true, new Tuple<>(4, "Dr. OBrien".length() - 4))), //this is valid
                new Tuple<>("Dr. McDonald", new Tuple<>(true, new Tuple<>(4, "Dr. McDonald".length() - 4))), //this is valid
                //new Tuple<>("John, M.D., Doe J.", new Tuple<>(true, new Tuple<>(0, "John, M.D., Doe J.".length()))), //this is valid
                new Tuple<>("JOHN RN, DEAN 5/2/2012", new Tuple<>(false, null)), //numbers shouldnt be allowed
                new Tuple<>("Dr.John's", new Tuple<>(true, new Tuple<>(3, "Dr.John's".length() - 3 - 2))), //this is valid

                new Tuple<>("John RN, Doe 5/2/2012", new Tuple<>(false, null)), //numbers shouldnt be allowed
                new Tuple<>("John (Jack) White, MD", new Tuple<>(true, new Tuple<>(0, "John (Jack) White, MD".length() - 4))),
                new Tuple<>("John RN, Doe", new Tuple<>(true, new Tuple<>(0, "John RN, Doe".length()))),
                new Tuple<>("John Doe,RN", new Tuple<>(true, new Tuple<>(0, "John Doe,RN".length() - 3))),
                new Tuple<>("Dr. Leathers MD", new Tuple<>(true, new Tuple<>(4, "Dr. Leathers MD".length() - 4 - 3))),
                new Tuple<>("Joe Leathers MD", new Tuple<>(true, new Tuple<>(0, "Joe Leathers MD".length() - 3))),
                new Tuple<>("John Doe RN", new Tuple<>(true, new Tuple<>(0, "John Doe RN".length() - 3))),
                new Tuple<>("John (Jack) White, MD.", new Tuple<>(true, new Tuple<>(0, "John (Jack) White, MD.".length() - 5))),
                
                new Tuple<>("John Doe RN CDE", new Tuple<>(true, new Tuple<>(0, "John Doe RN CDE".length() - 7))),
                new Tuple<>("John Doe RN/CDE", new Tuple<>(true, new Tuple<>(0, "John Doe RN/CDE".length() - 7))),
                new Tuple<>("Dr Leathers Doe", new Tuple<>(true, new Tuple<>(3, "Dr Leathers Doe".length() - 3))),
                
                new Tuple<>("JOHN, DON MD", new Tuple<>(true, new Tuple<>(0, "JOHN, DON MD".length() - 3))),

                new Tuple<>("JACK, JOE MD Hospital Visit", new Tuple<>(false, null)),
                
                new Tuple<>("Dr.Leathers", new Tuple<>(true, new Tuple<>(3, "Dr.Leathers".length() - 3))),
                new Tuple<>("Dr Leathers", new Tuple<>(true, new Tuple<>(3, "Dr Leathers".length() - 3))),
                new Tuple<>("Dr. Leathers Jonathan", new Tuple<>(true, new Tuple<>(4, "Dr. Leathers Jonathan".length() - 4))),
                new Tuple<>("Dr. Leathers ", new Tuple<>(true, new Tuple<>(4, "Dr. Leathers ".length() - 4 - 1))),
                
                new Tuple<>("Leathers", new Tuple<>(false, null)),
                new Tuple<>("John Smith", new Tuple<>(false, null)),

                new Tuple<>("Mr. Leathers", new Tuple<>(true, new Tuple<>(4, "Mr. Leathers".length() - 4))),
                new Tuple<>("Mr.Leathers", new Tuple<>(true, new Tuple<>(3, "Mr.Leathers".length() - 3))),
                new Tuple<>("Mr Leathers", new Tuple<>(true, new Tuple<>(3, "Mr Leathers".length() - 3))),
                new Tuple<>("Mr. Leathers", new Tuple<>(true, new Tuple<>(4, "Mr. Leathers".length() - 4))),
                new Tuple<>("MR. Leathers", new Tuple<>(true, new Tuple<>(4, "MR. Leathers".length() - 4))),
                new Tuple<>("Mr. Leathers Jonathan", new Tuple<>(true, new Tuple<>(4, "Mr. Leathers Jonathan".length() - 4))),
                new Tuple<>("Mr. Leathers ED", new Tuple<>(false, null)),
                new Tuple<>("Mr. Leathers ", new Tuple<>(true, new Tuple<>(4, "Mr. Leathers ".length() - 4 - 1))),
        
                new Tuple<>("Mr. John de la Cruz", new Tuple<>(true, new Tuple<>(4, "Mr. John de la Cruz".length() - 4))), 
                new Tuple<>("Mr. John van Riet-Lowe", new Tuple<>(true, new Tuple<>(4, "Mr. John van Riet-Lowe".length() - 4))),
                new Tuple<>("Mr. John de Tarr Smith", new Tuple<>(true, new Tuple<>(4, "Mr. John de Tarr Smith".length() - 4))),
                new Tuple<>("Mr. John van der Bent", new Tuple<>(true, new Tuple<>(4, "Mr. John van der Bent".length() - 4))),
                new Tuple<>("Mr. John van den Bent", new Tuple<>(true, new Tuple<>(4, "Mr. John van den Bent".length() - 4))),
                
                new Tuple<>("Johnson PA, Mary", new Tuple<>(true, new Tuple<>(0, "Johnson PA, Mary".length())))
        );

        for(Tuple<String, Tuple<Boolean, Tuple<Integer, Integer>>> expectedResultEntry: expectedResults) {
            String value = expectedResultEntry.getFirst();
            Tuple<Boolean, Tuple<Integer, Integer>> expectedResult = expectedResultEntry.getSecond();
            Tuple<Boolean, Tuple<Integer, Integer>> actualResult = nameIdentifier.isOfThisTypeWithOffset(value);

            assertEquals(expectedResult.getFirst(), actualResult.getFirst(), value);

            if (expectedResult.getFirst()) {
                assertEquals(expectedResult.getSecond(), actualResult.getSecond(), value);
            } else {
                assertNull(actualResult.getSecond(), value);
            }
        }
    }
}
