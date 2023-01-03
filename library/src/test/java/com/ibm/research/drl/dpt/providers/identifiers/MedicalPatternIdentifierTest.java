/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalPatternIdentifierTest {
    private final String[] testData = {
            "MRN: CLM-00000056055",
            "MRN:CLM-00000056055",
            "MRN:\t0001000231MDF",
            "MRN 03003030",
            "mrn 03003030",
            "Medical Record Number: 1234asds",
            "Medical Record Number 1234asds",
            "MRN #123342",
            "MR #123-34-22",
            "MR 123-34-22",
            "Patient ID: 1234567",
            "Patient ID 1234567",
            "Chart number: 123ABC-456efg",
            "Patient ID 123-45-67",
            "MRN# 1233456",
            "MRN: 122335",
            "MR#: 12-12-12-1",
            "MRN#123456",
            "MRN: 123445",
            "Medical Record # 12334234",
            // generic IDs
            "NIZ # 58627",
            "NIV# (comments here): xyz1234",
            "LOT # 1234324",
            "lot# 1234324",
            "auth# 12334",
            "NIA auth # 123XY8V",
            "Visit Note ID: 123445",
            "rpt # 12345557u",
            "Claim #: 1234432545",
            "Patient ID: 12345",
            "Member ID# 12345566",
            "Note ID: 12343455-1",

            //
            "order # 123123123",
            "order #123123123",
            "exam # 123123123",
            "exam #123123123",
            "Patient ID No: 2905264",
            "Patient ID No:2905264",
            "tracking # 123123",
            "tracking #123123",
            "dictation # 123123",
            "dictation #123123",
            "authorization #123123",
            "authorization # 123123",
            "MRN: 398375",
            
            "Medical Record #: 123456",
            "Approval Number: 123123"
    };

    private final String[] validationData = {
            "CLM-00000056055",
            "CLM-00000056055",
            "0001000231MDF",
            "03003030",
            "03003030",
            "1234asds",
            "1234asds",
            "123342",
            "123-34-22",
            "123-34-22",
            "1234567",
            "1234567",
            "123ABC-456efg",
            "123-45-67",
            "1233456",
            "122335",
            "12-12-12-1",
            "123456",
            "123445",
            "12334234",
            // generic IDs
            "58627",
            "xyz1234",
            "1234324",
            "1234324",
            "12334",
            "123XY8V",
            "123445",
            "12345557u",
            "1234432545",
            "12345",
            "12345566",
            "12343455-1",
            //
            "123123123",
            "123123123",
            "123123123",
            "123123123",
            "2905264",
            "2905264",
            "123123",
            "123123",
            "123123",
            "123123",
            "123123",
            "123123",
            "398375",
            "123456",
            "123123"
    };

    @Test
    public void validateExtractedID() {
        MedicalPatternIdentifier identifier = new MedicalPatternIdentifier();

        for (int i = 0; i < this.testData.length; ++i) {
            String data = this.testData[i];
            String validationData = this.validationData[i];

            Tuple<Boolean, Tuple<Integer, Integer>> identificationResult = identifier.isOfThisTypeWithOffset(data);

            assertNotNull(identificationResult, data);
            assertTrue(identificationResult.getFirst(), data);

            Tuple<Integer, Integer> identifiedRange = identificationResult.getSecond();

            assertNotNull(identifiedRange, data);

           String id = data.substring(identifiedRange.getFirst(), identifiedRange.getFirst() + identifiedRange.getSecond());

           assertEquals(validationData, id, data);
        }
    }

    @Test
    public void testIsInvalid() {
        MedicalPatternIdentifier identifier = new MedicalPatternIdentifier();
        
        String[] invalidPatterns = {
                "is",
                "mre",
                "MRI",
                "Authenticated",
                "ECHOCARDIOGRAM",
                "EXAM SELF",
                "exam room",
                "lotrisone",
                "MRN: Pending",
        };

        for (String data : invalidPatterns) {
            assertFalse(identifier.isOfThisType(data), data);
        }
        
    }
}