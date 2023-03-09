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

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AddressForFreeTextIdentifier extends AbstractIdentifier {

    private static final long serialVersionUID = -4924554398358986665L;

    private final static String DIRECTIONAL = "(?:" +
            "(?:(?:n|s|e|w|ne|nw|se|sw)\\.?)" +
            "|" +
            "(?:north|south|east|west|north[- ]?east|north[- ]?west|south[- ]?east|south[- ]west)" +
            ")";

    private final static String SUFFIX = "(?:(?:" +
            "ALLEE|ALLEY|ALLY|ALY|" +
            "ANEX|ANNEX|ANNX|ANX|" +
            "ARC|ARCADE|" +
            "AV|AVE|AVEN|AVENU|AVENUE|AVN|AVNUE|" +
            "BAYOO|BAYOU|" +
            "BCH|BEACH|" +
            "BEND|BND|" +
            "BG|" +
            "BGS|" +
            "BLF|BLUF|BLUFF|" +
            "BLFS|BLUFFS|" +
            "BLVD|BOUL|BOULEVARD|BOULV|" +
            "BOT|BOTTM|BOTTOM|" +
            "BR|" +
            "BRANCH|" +
            "BRDGE|" +
            "BRG|" +
            "BRIDGE|" +
            "BRK|" +
            "BRKS|" +
            "BRNCH|" +
            "BROOK|" +
            "BROOKS|" +
            "BTM|" +
            "BURG|" +
            "BURGS|" +
            "BYP|" +
            "BYPA|" +
            "BYPAS|" +
            "BYPASS|" +
            "BYPS|" +
            "BYU|" +
            "CAMP|" +
            "CANYN|" +
            "CANYON|" +
            "CAPE|" +
            "CAUSEWAY|" +
            "CAUSWA|" +
            "CEN|" +
            "CENT|" +
            "CENTER|" +
            "CENTERS|" +
            "CENTR|" +
            "CENTRE|" +
            "CIR|" +
            "CIRC|" +
            "CIRCL|" +
            "CIRCLE|" +
            "CIRCLES|" +
            "CIRS|" +
            "CLB|" +
            "CLF|" +
            "CLFS|" +
            "CLIFF|" +
            "CLIFFS|" +
            "CLUB|" +
            "CMN|" +
            "CMNS|" +
            "CMP|" +
            "CNTER|" +
            "CNTR|" +
            "CNYN|" +
            "COMMON|" +
            "COMMONS|" +
            "COR|" +
            "CORNER|" +
            "CORNERS|" +
            "CORS|" +
            "COURSE|" +
            "COURT|" +
            "COURTS|" +
            "COVE|" +
            "COVES|" +
            "CP|" +
            "CPE|" +
            "CRCL|" +
            "CRCLE|" +
            "CREEK|" +
            "CRES|" +
            "CRESCENT|" +
            "CREST|" +
            "CRK|" +
            "CROSSING|" +
            "CROSSROAD|" +
            "CROSSROADS|" +
            "CRSE|" +
            "CRSENT|" +
            "CRSNT|" +
            "CRSSNG|" +
            "CRST|" +
            "CSWY|" +
            "CT\\.|" +
            "CTR|" +
            "CTRS|" +
            "CTS|" +
            "CURV|" +
            "CURVE|" +
            "CV|" +
            "CVS|" +
            "CYN|" +
            "DALE|" +
            "DAM|" +
            "DIV|" +
            "DIVIDE|" +
            "DL|" +
            "DM|" +
            "DR|" +
            "DRIV|" +
            "DRIVE|" +
            "DRIVES|" +
            "DRS|" +
            "DRV|" +
            "DV|" +
            "DVD|" +
            "EST|" +
            "ESTATE|" +
            "ESTATES|" +
            "ESTS|" +
            "EXP|" +
            "EXPR|" +
            "EXPRESS|" +
            "EXPRESSWAY|" +
            "EXPW|" +
            "EXPY|" +
            "EXT|" +
            "EXTENSION|" +
            "EXTENSIONS|" +
            "EXTN|" +
            "EXTNSN|" +
            "EXTS|" +
            "FALL|" +
            "FALLS|" +
            "FERRY|" +
            "FIELD|" +
            "FIELDS|" +
            "FLAT|" +
            "FLATS|" +
            "FLD|" +
            "FLDS|" +
            "FLS|" +
            "FLT|" +
            "FLTS|" +
            "FORD|" +
            "FORDS|" +
            "FOREST|" +
            "FORESTS|" +
            "FORG|" +
            "FORGE|" +
            "FORGES|" +
            "FORK|" +
            "FORKS|" +
            "FORT|" +
            "FRD|" +
            "FRDS|" +
            "FREEWAY|" +
            "FREEWY|" +
            "FRG|" +
            "FRGS|" +
            "FRK|" +
            "FRKS|" +
            "FRRY|" +
            "FRST|" +
            "FRT|" +
            "FRWAY|" +
            "FRWY|" +
            "FRY|" +
            "FT|" +
            "FWY|" +
            "GARDEN|" +
            "GARDENS|" +
            "GARDN|" +
            "GATEWAY|" +
            "GATEWY|" +
            "GATWAY|" +
            "GDN|" +
            "GDNS|" +
            "GLEN|" +
            "GLENS|" +
            "GLN|" +
            "GLNS|" +
            "GRDEN|" +
            "GRDN|" +
            "GRDNS|" +
            "GREEN|" +
            "GREENS|" +
            "GRN|" +
            "GRNS|" +
            "GROV|" +
            "GROVE|" +
            "GROVES|" +
            "GRV|" +
            "GRVS|" +
            "GTWAY|" +
            "GTWY|" +
            "HARB|" +
            "HARBOR|" +
            "HARBORS|" +
            "HARBR|" +
            "HAVEN|" +
            "HBR|" +
            "HBRS|" +
            "HEIGHTS|" +
            "HIGHWAY|" +
            "HIGHWY|" +
            "HILL|" +
            "HILLS|" +
            "HIWAY|" +
            "HIWY|" +
            "HL|" +
            "HLLW|" +
            "HLS|" +
            "HOLLOW|" +
            "HOLLOWS|" +
            "HOLW|" +
            "HOLWS|" +
            "HRBOR|" +
            "HT|" +
            "HTS|" +
            "HVN|" +
            "HWAY|" +
            "HWY|" +
            "INLET|" +
            "INLT|" +
            //"IS|" +
            "ISLAND|" +
            "ISLANDS|" +
            "ISLE|" +
            "ISLES|" +
            "ISLND|" +
            "ISLNDS|" +
            "ISS|" +
            "JCT|" +
            "JCTION|" +
            "JCTN|" +
            "JCTNS|" +
            "JCTS|" +
            "JUNCTION|" +
            "JUNCTIONS|" +
            "JUNCTN|" +
            "JUNCTON|" +
            "KEY|" +
            "KEYS|" +
            "KNL|" +
            "KNLS|" +
            "KNOL|" +
            "KNOLL|" +
            "KNOLLS|" +
            "KY|" +
            "KYS|" +
            "LAKE|" +
            "LAKES|" +
            "LAND|" +
            "LANDING|" +
            "LANE|" +
            "LCK|" +
            "LCKS|" +
            "LDG|" +
            "LDGE|" +
            "LF|" +
            "LGT|" +
            "LGTS|" +
            "LIGHT|" +
            "LIGHTS|" +
            "LK|" +
            "LKS|" +
            "LN|" +
            "LNDG|" +
            "LNDNG|" +
            "LOAF|" +
            "LOCK|" +
            "LOCKS|" +
            "LODG|" +
            "LODGE|" +
            "LOOP|" +
            "LOOPS|" +
            "MALL|" +
            "MANOR|" +
            "MANORS|" +
            "MDW|" +
            "MDWS|" +
            "MEADOW|" +
            "MEADOWS|" +
            "MEDOWS|" +
            "MEWS|" +
            "MILL|" +
            "MILLS|" +
            "MISSION|" +
            "MISSN|" +
            "ML|" +
            "MLS|" +
            "MNR|" +
            "MNRS|" +
            "MNT|" +
            "MNTAIN|" +
            "MNTN|" +
            "MNTNS|" +
            "MOTORWAY|" +
            "MOUNT|" +
            "MOUNTAIN|" +
            "MOUNTAINS|" +
            "MOUNTIN|" +
            "MSN|" +
            "MSSN|" +
            "MT|" +
            "MTIN|" +
            "MTN|" +
            "MTNS|" +
            "MTWY|" +
            "NCK|" +
            //"NECK|" +
            "OPAS|" +
            "ORCH|" +
            "ORCHARD|" +
            "ORCHRD|" +
            "OVAL|" +
            "OVERPASS|" +
            "OVL|" +
            "PARK|" +
            "PARKS|" +
            "PARKWAY|" +
            "PARKWAYS|" +
            "PARKWY|" +
            "PASS|" +
            "PASSAGE|" +
            "PATH|" +
            "PATHS|" +
            "PIKE|" +
            "PIKES|" +
            "PINE|" +
            "PINES|" +
            "PKWAY|" +
            "PKWY|" +
            "PKWYS|" +
            "PKY|" +
            "PL|" +
            "PLACE|" +
            "PLAIN|" +
            "PLAINS|" +
            "PLAZA|" +
            "PLN|" +
            "PLNS|" +
            "PLZ|" +
            "PLZA|" +
            "PNE|" +
            "PNES|" +
            "POINT|" +
            "POINTS|" +
            "PORT|" +
            "PORTS|" +
            "PR|" +
            "PRAIRIE|" +
            "PRK|" +
            "PRR|" +
            "PRT|" +
            "PRTS|" +
            "PSGE|" +
            "PT|" +
            "PTS|" +
            "RAD|" +
            "RADIAL|" +
            "RADIEL|" +
            "RADL|" +
            "RAMP|" +
            "RANCH|" +
            "RANCHES|" +
            "RAPID|" +
            "RAPIDS|" +
            "RD|" +
            "RDG|" +
            "RDGE|" +
            "RDGS|" +
            "RDS|" +
            "REST|" +
            "RIDGE|" +
            "RIDGES|" +
            "RIV|" +
            "RIVER|" +
            "RIVR|" +
            "RNCH|" +
            "RNCHS|" +
            "ROAD|" +
            "ROADS|" +
            "(?:ROUTE|RTE|RT)\\s+\\d+|" +
            "ROW|" +
            "RPD|" +
            "RPDS|" +
            "RST|" +
            "RUE|" +
            "RUN|" +
            "RVR|" +
            "SHL|" +
            "SHLS|" +
            "SHOAL|" +
            "SHOALS|" +
            "SHOAR|" +
            "SHOARS|" +
            "SHORE|" +
            "SHORES|" +
            "SHR|" +
            "SHRS|" +
            "SKWY|" +
            "SKYWAY|" +
            "SMT|" +
            "SPG|" +
            "SPGS|" +
            "SPNG|" +
            "SPNGS|" +
            "SPRING|" +
            "SPRINGS|" +
            "SPRNG|" +
            "SPRNGS|" +
            "SPUR|" +
            "SPURS|" +
            "SQ|" +
            "SQR|" +
            "SQRE|" +
            "SQRS|" +
            "SQS|" +
            "SQU|" +
            "SQUARE|" +
            "SQUARES|" +
            "ST|" +
            "STA|" +
            "STATION|" +
            "STATN|" +
            "STN|" +
            "STR|" +
            "STRA|" +
            "STRAV|" +
            "STRAVEN|" +
            "STRAVENUE|" +
            "STRAVN|" +
            "STREAM|" +
            "STREET|" +
            "STREETS|" +
            "STREME|" +
            "STRM|" +
            "STRT|" +
            "STRVN|" +
            "STRVNUE|" +
            "STS|" +
            "SUMIT|" +
            "SUMITT|" +
            "SUMMIT|" +
            "TER|" +
            "TERR|" +
            "TERRACE|" +
            "THROUGHWAY|" +
            "TPKE|" +
            "TRACE|" +
            "TRACES|" +
            "TRACK|" +
            "TRACKS|" +
            "TRAFFICWAY|" +
            "TRAIL|" +
            "TRAILER|" +
            "TRAILS|" +
            "TRAK|" +
            "TRCE|" +
            "TRFY|" +
            "TRK|" +
            "TRKS|" +
            "TRL|" +
            "TRLR|" +
            "TRLRS|" +
            "TRLS|" +
            "TRNPK|" +
            "TRWY|" +
            "TUNEL|" +
            "TUNL|" +
            "TUNLS|" +
            "TUNNEL|" +
            "TUNNELS|" +
            "TUNNL|" +
            "TURNPIKE|" +
            "TURNPK|" +
            "UN|" +
            "UNDERPASS|" +
            "UNION|" +
            "UNIONS|" +
            "UNS|" +
            "UPAS|" +
            "VALLEY|" +
            "VALLEYS|" +
            "VALLY|" +
            "VDCT|" +
            "VIA|" +
            "VIADCT|" +
            "VIADUCT|" +
            "VIEW|" +
            "VIEWS|" +
            "VILL|" +
            "VILLAG|" +
            "VILLAGE|" +
            "VILLAGES|" +
            "VILLE|" +
            "VILLG|" +
            "VILLIAGE|" +
            "VIS|" +
            "VIST|" +
            "VISTA|" +
            "VL|" +
            "VLG|" +
            "VLGS|" +
            "VLLY|" +
            "VLY|" +
            "VLYS|" +
            "VST|" +
            "VSTA|" +
            "VW|" +
            "VWS|" +
            "WALK|" +
            "WALKS|" +
            "WALL|" +
            "WAY|" +
            "WAYS|" +
            "WELL|" +
            "WELLS|" +
            "WL|" +
            "WLS|" +
            "WY|" +
            "XING|" +
            "XRD|" +
            "XRDS" +
            ")\\.?)"; // suffix

    private static final String SECONDARY_ADDRESS = "(?:" +
            "#|" +
            "APT\\.?|" +
            "BSMT|" +
            "BLDG|" +
            "DEPT|" +
            "FL|" +
            "FRNT|" +
            "HNGR|" +
            "KEY|" +
            "LBBY|" +
            "LOT|" +
            "LOWR|" +
            "OFC|" +
            "PH|" +
            "PIER|" +
            "REAR|" +
            "RM|" +
            "SIDE|" +
            "SLIP|" +
            "SPC|" +
            "STOP|" +
            "STE|" +
            "TRLR|" +
            "UNIT|" +
            "UPPR" +
            ")" +
            "\\s*" +
            "(?:(?:(?:#\\s*)?\\d+)|\\p{Alpha}+)";

    private final static String DELIVERY_ADDRESS_LINE =
            "\\d+(?:-\\d+)?" + // primary address number
                    "(?:\\s+" + DIRECTIONAL + ")?" + // pre directional
                    "(?:\\s+\\p{Alpha}{3,}){1,4}(?:\\s+\\d+)?" + // street name
                    "\\s+" + SUFFIX + // suffix
                    "(?: " + DIRECTIONAL + ")?" + // post directional
                    "(?:\\s+" + SECONDARY_ADDRESS + ")?"  // secondary address identifier
            ;

    private static final String STATE_AND_POSSESSIONS = "(?:" +
            "AK|" +
            "AL|" +
            "AR|" +
            "AS|" +
            "AZ|" +
            "CA|" +
            "CO|" +
            "CT|" +
            "DC|" +
            "DE|" +
            "FL|" +
            "FM|" +
            "GA|" +
            "GU|" +
            "HI|" +
            "IA|" +
            "ID|" +
            "IL|" +
            "IN|" +
            "KS|" +
            "KY|" +
            "LA|" +
            "MA|" +
            "MD|" +
            "ME|" +
            "MH|" +
            "MI|" +
            "MN|" +
            "MO|" +
            "MP|" +
            "MS|" +
            "MT|" +
            "NC|" +
            "ND|" +
            "NE|" +
            "NH|" +
            "NJ|" +
            "NM|" +
            "NV|" +
            "NY|" +
            "OH|" +
            "OK|" +
            "OR|" +
            "PA|" +
            "PR|" +
            "PW|" +
            "RI|" +
            "SC|" +
            "SD|" +
            "TN|" +
            "TX|" +
            "UT|" +
            "VA|" +
            "VI|" +
            "VT|" +
            "WA|" +
            "WI|" +
            "WV|" +
            "WY" +
            ")";

    private final static String SEPARATOR = ",?\\s*";
    private final static String ZIP_CODE = "(?:\\d{5}(?:-?\\d{4})?)";

    private final static String CITY_NAME = "(?:\\p{Alnum}{2,}(?:\\s+|-)){0,4}\\p{Alnum}{3,}";

    private final static String COUNTRY = "USA|U.S.A.|U.S.|United\\s+States\\s+of\\s+America";

    private static final List<Pattern> patterns = Arrays.asList(
            Pattern.compile(
                    DELIVERY_ADDRESS_LINE, Pattern.CASE_INSENSITIVE
            ),
            Pattern.compile(
                    DELIVERY_ADDRESS_LINE + SEPARATOR + CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS
                    //, Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    DELIVERY_ADDRESS_LINE + SEPARATOR + CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS + SEPARATOR + ZIP_CODE,
                    Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    DELIVERY_ADDRESS_LINE + SEPARATOR + CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS + SEPARATOR + ZIP_CODE + SEPARATOR + COUNTRY,
                    Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    SECONDARY_ADDRESS + SEPARATOR + DELIVERY_ADDRESS_LINE + SEPARATOR + CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS + SEPARATOR + ZIP_CODE
                    , Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS + SEPARATOR + ZIP_CODE
                    , Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    SECONDARY_ADDRESS + SEPARATOR + CITY_NAME + SEPARATOR + STATE_AND_POSSESSIONS + SEPARATOR + ZIP_CODE
                    , Pattern.CASE_INSENSITIVE
            )
            , Pattern.compile(
                    STATE_AND_POSSESSIONS, Pattern.CASE_INSENSITIVE
            ),
            Pattern.compile(
                    "\\d+(?:-\\d+)?" + // primary address number
                            "(?:\\s+" + DIRECTIONAL + ")?" + // pre directional
                            "(?:\\s+\\p{Alpha}{3,}){1,4}(?:\\s+\\d+)?" + // street name
                            "\\s+" + SUFFIX + // suffix
                            "(?: " + DIRECTIONAL + ")?" + // post directional
                            "(?:\\s+" + SECONDARY_ADDRESS + ")?" +
                            "(?:" + SEPARATOR + "(?:\\s+\\p{Alpha}{3,}){1,4})?",
                    Pattern.CASE_INSENSITIVE
            )
    );

    @Override
    public ProviderType getType() {
        return ProviderType.ADDRESS;
    }

    private boolean quickCheck(final String data) {
        for (int i = 0; i < data.length(); ++i) {
            char ith = data.charAt(i);
            if (Character.isWhitespace(ith)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOfThisType(String data) {
        if (data.length() < 5 || data.length() >= 128 || !quickCheck(data)) {
            return false;
        }

        if (!checkThatCaseIsConsistent(data)) {
            return false;
        }

        for (Pattern pattern : patterns) {
            if (pattern.matcher(data).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean checkThatCaseIsConsistent(String data) {
        String[] tokens = data.split("[\\s+|,]");

        int countUppercase = 0;
        int countLowercase = 0;

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            char c = token.charAt(0);
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    countLowercase++;
                } else {
                    countUppercase++;
                }
            }
        }

        return (!(countLowercase > 0 && countUppercase > 0));
    }

    @Override
    public String getDescription() {
        return "Address identifier for free text. It assumes to be executed only from PRIMAIdentifier.";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.SPACE;
    }

    @Override
    public int getMinimumLength() {
        return 5;
    }

    @Override
    public int getMaximumLength() {
        return 128;
    }
}
