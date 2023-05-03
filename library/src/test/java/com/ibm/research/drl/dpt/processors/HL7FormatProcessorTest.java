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
package com.ibm.research.drl.dpt.processors;


import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.HashMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HL7FormatProcessorTest {

    @Test
    public void testFromProcessorFactory() throws Exception {

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);

        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "AL1||SEV|003^DUST\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";

        try (FileOutputStream fos = new FileOutputStream("/tmp/hl7.txt");) {
            fos.write(msg.getBytes());
        }

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        try (InputStream configInputStream = HL7FormatProcessorTest.class.getResourceAsStream("/hl7mask.json");
        InputStream optionsInputStream = HL7FormatProcessorTest.class.getResourceAsStream("/hl7options.json");) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(configInputStream));
            DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(optionsInputStream, DataMaskingOptions.class);

            formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String xref = "/.MSH-3-1";

                String maskedSendingApp = terser.get(xref);
                assertNotEquals("HIS", maskedSendingApp);
            }
        }
    }

    @Test
    public void testHL7v22() throws Exception {

        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "AL1||SEV|003^DUST\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";

        String xref = "/.MSH-3-1";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        try (InputStream config = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22.json");
        InputStream options = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(config));
            DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(options, DataMaskingOptions.class);

            FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
            formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String maskedSendingApp = terser.get(xref);

                assertNotEquals("HIS", maskedSendingApp);
                assertEquals(3, maskedSendingApp.length());
            }
        }
    }

    @Test
    public void testHL7v23() throws Exception {

        String msg = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v23|T|2.3\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String xref = "/.MSH-3-1";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        try (InputStream config = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22.json");
             InputStream options = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(config));
            DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(options, DataMaskingOptions.class);

            FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
            formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String maskedSendingApp = terser.get(xref);
                assertNotEquals("ULTRA", maskedSendingApp);
                assertEquals(5, maskedSendingApp.length());
            }
        }
    }

    @Test
    public void testHL7v23InvalidXref() throws Exception {

        String msg = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v23|T|2.3\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";

        String xref = "/.MSH-3-1";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        try (InputStream config = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22invalidxref.json");
        InputStream options = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22invalidxref.json")) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(config));
            DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(options, DataMaskingOptions.class);

            FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
            formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String maskedSendingApp = terser.get(xref);
                assertEquals("ULTRA", maskedSendingApp);
            }
        }
    }


    @Test
    public void testHL7PerFieldConfiguration() throws Exception {

        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "AL1||SEV|003^DUST\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";

        String xref = "/.MSH-3-1";

        try (InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        InputStream config = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22perfield.json");
        InputStream options = HL7FormatProcessorTest.class.getResourceAsStream("/hl7v22perfield.json");
        ) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(config));
            DataMaskingOptions maskingOptions = JsonUtils.MAPPER.readValue(options, DataMaskingOptions.class);

            FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.HL7);
            formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String maskedSendingApp = terser.get(xref);
                assertNotEquals("HIS", maskedSendingApp);

                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update("HIS".getBytes());
                byte[] expected = md.digest();

                assertEquals(maskedSendingApp, HashMaskingProvider.bytesToHex(expected));
            }
        }
    }

    @Test
    public void testSuppressShouldDropField() throws Exception {
        try (
            InputStream inputStream = new ByteArrayInputStream((
                "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r"
                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r"
                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r"
                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r"
                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r"
                + "AL1||SEV|001^POLLEN\r"
                + "AL1||SEV|003^DUST\r"
                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\r"
                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554"
            ).getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String xref = "/.MSH-3-1";

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put(xref, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, xref));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    toBeMasked, false, null, null);
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);
            new HL7FormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(), Collections.emptyMap());

            String masked = outputStream.toString();

            try (HapiContext context = new DefaultHapiContext();) {
                Parser p = context.getGenericParser();
                Message maskedMessage = p.parse(masked);
                Terser terser = new Terser(maskedMessage);

                String maskedValue = terser.get(xref);
                assertThat(maskedValue, nullValue());
            }
        }
    }
}
