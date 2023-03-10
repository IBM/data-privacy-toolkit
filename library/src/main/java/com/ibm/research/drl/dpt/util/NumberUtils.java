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
package com.ibm.research.drl.dpt.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NumberUtils {
    private static final Set<String> allowedStrings = Set.of(
            "zero", "one", "two", "three", "four", "five", "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
            "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
            "hundred", "thousand", "million", "billion", "trillion");

    private final static Map<String, String> ordinalNumbers = initOrdinalNumbers();

    private static Map<String, String> initOrdinalNumbers() {
        Map<String, String> ordinalNumbers = new HashMap<>();
        ordinalNumbers.put("first", "one");
        ordinalNumbers.put("second", "two");
        ordinalNumbers.put("third", "three");
        ordinalNumbers.put("fourth", "four");
        ordinalNumbers.put("fifth", "five");
        ordinalNumbers.put("sixth", "six");
        ordinalNumbers.put("seventh", "seven");
        ordinalNumbers.put("eighth", "eight");
        ordinalNumbers.put("ninth", "nine");
        ordinalNumbers.put("tenth", "ten");
        ordinalNumbers.put("eleventh", "eleven");
        ordinalNumbers.put("twelfth", "twelve");
        ordinalNumbers.put("thirteenth", "thirteen");
        ordinalNumbers.put("fourteenth", "fourteen");
        ordinalNumbers.put("fifteenth", "fifteen");
        ordinalNumbers.put("sixteenth", "sixteen");
        ordinalNumbers.put("seventeenth", "seventeen");
        ordinalNumbers.put("eighteenth", "eighteen");
        ordinalNumbers.put("nineteenth", "nineteen");
        ordinalNumbers.put("twentieth", "twenty");
        ordinalNumbers.put("thirtieth", "thirty");
        ordinalNumbers.put("fortieth", "forty");
        ordinalNumbers.put("fiftieth", "fifty");
        ordinalNumbers.put("sixtieth", "sixty");
        ordinalNumbers.put("seventieth", "seventy");
        ordinalNumbers.put("eightieth", "eighty");
        ordinalNumbers.put("ninetieth", "ninety");
        ordinalNumbers.put("hundredth", "hundred");
        ordinalNumbers.put("thousandth", "thousand");
        ordinalNumbers.put("millionth", "million");

        return ordinalNumbers;
    }

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] numNames = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " hundred" + soFar;
    }

    public static String createWords(long number) {
        if (number == 0) {
            return "zero";
        }

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        String sNumber = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(sNumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(sNumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(sNumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(sNumber.substring(9, 12));

        String tradBillions;
        if (billions == 0) {
            tradBillions = "";
        } else {
            tradBillions = convertLessThanOneThousand(billions) + " billion ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
            default:
                tradMillions = convertLessThanOneThousand(millions) + " million ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "one thousand ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " thousand ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ").trim();
    }


    public static Long createNumberOrder(String input) {
        long result = 0;
        long finalResult = 0;

        if (input == null || input.length() == 0) {
            return null;
        }

        input = input.replaceAll("-", " ");
        input = input.toLowerCase().replaceAll(" and", " ");
        String[] splitParts = input.trim().split("\\s+");

        for (int i = 0; i < splitParts.length; i++) {
            String str = splitParts[i];
            if (ordinalNumbers.containsKey(str)) {
                splitParts[i] = ordinalNumbers.get(str);
            }

            if (!allowedStrings.contains(splitParts[i])) {
                return null;
            }
        }

        for (String str : splitParts) {
            if (str.equalsIgnoreCase("zero")) {
                result += 0;
            } else if (str.equalsIgnoreCase("one")) {
                result += 1;
            } else if (str.equalsIgnoreCase("two")) {
                result += 2;
            } else if (str.equalsIgnoreCase("three")) {
                result += 3;
            } else if (str.equalsIgnoreCase("four")) {
                result += 4;
            } else if (str.equalsIgnoreCase("five")) {
                result += 5;
            } else if (str.equalsIgnoreCase("six")) {
                result += 6;
            } else if (str.equalsIgnoreCase("seven")) {
                result += 7;
            } else if (str.equalsIgnoreCase("eight")) {
                result += 8;
            } else if (str.equalsIgnoreCase("nine")) {
                result += 9;
            } else if (str.equalsIgnoreCase("ten")) {
                result += 10;
            } else if (str.equalsIgnoreCase("eleven")) {
                result += 11;
            } else if (str.equalsIgnoreCase("twelve")) {
                result += 12;
            } else if (str.equalsIgnoreCase("thirteen")) {
                result += 13;
            } else if (str.equalsIgnoreCase("fourteen")) {
                result += 14;
            } else if (str.equalsIgnoreCase("fifteen")) {
                result += 15;
            } else if (str.equalsIgnoreCase("sixteen")) {
                result += 16;
            } else if (str.equalsIgnoreCase("seventeen")) {
                result += 17;
            } else if (str.equalsIgnoreCase("eighteen")) {
                result += 18;
            } else if (str.equalsIgnoreCase("nineteen")) {
                result += 19;
            } else if (str.equalsIgnoreCase("twenty")) {
                result += 20;
            } else if (str.equalsIgnoreCase("thirty")) {
                result += 30;
            } else if (str.equalsIgnoreCase("forty")) {
                result += 40;
            } else if (str.equalsIgnoreCase("fifty")) {
                result += 50;
            } else if (str.equalsIgnoreCase("sixty")) {
                result += 60;
            } else if (str.equalsIgnoreCase("seventy")) {
                result += 70;
            } else if (str.equalsIgnoreCase("eighty")) {
                result += 80;
            } else if (str.equalsIgnoreCase("ninety")) {
                result += 90;
            } else if (str.equalsIgnoreCase("hundred")) {
                result *= 100;
            } else if (str.equalsIgnoreCase("thousand")) {
                result *= 1000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("million")) {
                result *= 1000000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("billion")) {
                result *= 1000000000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("trillion")) {
                result *= 1000000000000L;
                finalResult += result;
                result = 0;
            }
        }

        finalResult += result;
        return finalResult;
    }

    public static Long createNumber(String input) {
        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;

        if (input == null || input.length() == 0) {
            return null;
        }

        input = input.replaceAll("-", " ");
        input = input.toLowerCase().replaceAll(" and", " ");
        String[] splitParts = input.trim().split("\\s+");

        for (String str : splitParts) {
            if (!allowedStrings.contains(str)) {
                isValidInput = false;
                break;
            }
        }

        if (!isValidInput) {
            return null;
        }

        for (String str : splitParts) {
            if (str.equalsIgnoreCase("zero")) {
                result += 0;
            } else if (str.equalsIgnoreCase("one")) {
                result += 1;
            } else if (str.equalsIgnoreCase("two")) {
                result += 2;
            } else if (str.equalsIgnoreCase("three")) {
                result += 3;
            } else if (str.equalsIgnoreCase("four")) {
                result += 4;
            } else if (str.equalsIgnoreCase("five")) {
                result += 5;
            } else if (str.equalsIgnoreCase("six")) {
                result += 6;
            } else if (str.equalsIgnoreCase("seven")) {
                result += 7;
            } else if (str.equalsIgnoreCase("eight")) {
                result += 8;
            } else if (str.equalsIgnoreCase("nine")) {
                result += 9;
            } else if (str.equalsIgnoreCase("ten")) {
                result += 10;
            } else if (str.equalsIgnoreCase("eleven")) {
                result += 11;
            } else if (str.equalsIgnoreCase("twelve")) {
                result += 12;
            } else if (str.equalsIgnoreCase("thirteen")) {
                result += 13;
            } else if (str.equalsIgnoreCase("fourteen")) {
                result += 14;
            } else if (str.equalsIgnoreCase("fifteen")) {
                result += 15;
            } else if (str.equalsIgnoreCase("sixteen")) {
                result += 16;
            } else if (str.equalsIgnoreCase("seventeen")) {
                result += 17;
            } else if (str.equalsIgnoreCase("eighteen")) {
                result += 18;
            } else if (str.equalsIgnoreCase("nineteen")) {
                result += 19;
            } else if (str.equalsIgnoreCase("twenty")) {
                result += 20;
            } else if (str.equalsIgnoreCase("thirty")) {
                result += 30;
            } else if (str.equalsIgnoreCase("forty")) {
                result += 40;
            } else if (str.equalsIgnoreCase("fifty")) {
                result += 50;
            } else if (str.equalsIgnoreCase("sixty")) {
                result += 60;
            } else if (str.equalsIgnoreCase("seventy")) {
                result += 70;
            } else if (str.equalsIgnoreCase("eighty")) {
                result += 80;
            } else if (str.equalsIgnoreCase("ninety")) {
                result += 90;
            } else if (str.equalsIgnoreCase("hundred")) {
                result *= 100;
            } else if (str.equalsIgnoreCase("thousand")) {
                result *= 1000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("million")) {
                result *= 1000000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("billion")) {
                result *= 1000000000;
                finalResult += result;
                result = 0;
            } else if (str.equalsIgnoreCase("trillion")) {
                result *= 1000000000000L;
                finalResult += result;
                result = 0;
            }
        }

        finalResult += result;
        return finalResult;
    }

    public static long countDigits(String data) {
        long cnt = 0;

        for (int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                cnt++;
            }
        }

        return cnt;
    }

    /*
    Returns the value with only the digits to keep
    -1 means keep all digits
     */
    public static String trimDecimalDigits(String identifier, int digitsToKeep) {

        if (digitsToKeep == -1) {
            return identifier;
        }

        int idx = identifier.indexOf('.');
        if (idx == -1) {
            return identifier;
        }

        if (digitsToKeep == 0) {
            return identifier.substring(0, idx);
        }

        if (identifier.length() < (idx + 1 + digitsToKeep)) {
            return identifier;
        }

        return identifier.substring(0, idx + 1 + digitsToKeep);
    }
}
