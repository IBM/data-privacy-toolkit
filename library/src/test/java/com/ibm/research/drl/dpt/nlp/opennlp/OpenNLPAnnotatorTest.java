/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.opennlp;


import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.Language;
import com.ibm.research.drl.dpt.nlp.NLPAnnotator;
import com.ibm.research.drl.dpt.nlp.NLPUtils;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

public class OpenNLPAnnotatorTest {
    private final String note1 = "Peter was not home at present. His wife picked up the phone and took a message for him to return my call so that I could talk to him about his healthcare management. I will try to reach out to Peter again next week if no call returned  prior. Peter appears to work so I might have to try him at a later hour";

    private final String note2 = "Called pt home phone number listed at 555-000-1111.  I asked for Ms. Ball and the phone was handed off to what sound ed like an elderly women from someone who appeared to be a caregiver.  I asked if it was Ms. Ball and she said yes and  I told her who I was and that I was calling with Dr. Hammonds office where then she told me that she doesnt know who that is.  I then asked if her first name was Chloe and she responded in a way that I couldnt understand what she was saying.  But that is not her first name and she didnt know who I was asking for.  She thanked me and hung up the phone.   Because this isnt my doctor and I have never spoken with pt before I did not want to try the cell phone that was listed.  This message will be written so that Lilian the primary RN CC can read it and decide what she would like to do from here";

    @Test
    public void testIdentification() throws Exception {
        //instantiate a new NLPAnnotator
        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            //we invoke the NLPAnnotator. It will return a list of identified tokens per sentence.
            final List<IdentifiedEntity> identifiedEntities = identifier.identify(note1, Language.UNKNOWN);

            //NLPUtils.createString is a utility function to convert a list of identified tokens in a string with annotation information
            //example print: <ProviderType:NAME>Peter</ProviderType> was not home at present
            System.out.println("sentence:" + NLPUtils.createString(identifiedEntities));

            //for each token you can get the value and the ProviderType (PRIMA data type class):
            for (final IdentifiedEntity original : identifiedEntities) {
                System.out.println("\ttoken:" + original.getText() + ", type:" + original.getType());
            }
        }
    }

    @Test
    public void testNotesMultipleLanguages() throws Exception {
        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp_multiple_languages.json")) {
            final NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));
            System.out.println(NLPUtils.createString(identifier.identify(note1, Language.ENGLISH)));

            String sentenceInDutch = "Mijn naam is John";
            System.out.println(NLPUtils.createString(identifier.identify(sentenceInDutch, Language.DUTCH)));
        }
    }

    @Test
    public void testNotes() throws Exception {
        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));
            System.out.println(NLPUtils.createString(identifier.identify(note1, Language.UNKNOWN)));

            System.out.println("\n---- note 2 ----\n");
            System.out.println(NLPUtils.createString(identifier.identify(note2, Language.UNKNOWN)));
        }
    }

    @Test
    @Disabled
    public void testIdentifyText() throws Exception {
        String text = "John is married, 35 years old and works at IBM";
        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            List<IdentifiedEntity> results = identifier.identify(text, Language.UNKNOWN);

            for (IdentifiedEntity result : results) {
                System.out.println("Result: " + result);
            }
        }
    }
    
    @Test
    @Disabled
    public void testNLPIdentifier() throws Exception {
        String text = "John is married, 35 years old and works at IBM. Last week he went to Rome, Italy for a conference. His e-mail is santonat@ie.ibm.com.";

        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));
            System.out.println(NLPUtils.createString(identifier.identify(text, Language.UNKNOWN)));
        }
    }

    @Test
    @Disabled
    public void testNLPIdentifierEmail() throws Exception {
        String email = "From: john_doe@us.ibm.com\n" +
                "\n" +
                "To: jane_doe@us.ibm.com\n" +
                "\n" +
                "Subject: On the topic of Suzanne Doe\n" +
                "\n" +
                "Last week on Monday (08-12-1981) in Timbucktoo, we decided to call Spiros at 235-555-1245. He woke up at 8am.\n" +
                "\n" +
                "Regards,\n" +
                "\n" +
                "John Doe\n" +
                "\n" +
                "Senior Research Scientist\n" +
                "IBM";

        try (InputStream inputStream = OpenNLPAnnotatorTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));
            System.out.println(NLPUtils.createString(identifier.identify(email, Language.UNKNOWN)));
        }
    }
}
