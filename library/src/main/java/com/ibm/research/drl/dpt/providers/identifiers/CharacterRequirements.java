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

/**
 * Bitmask constants for specifying required character classes in an identifier.
 */
public class CharacterRequirements {
    /** No character class requirement. */
    public static final int NONE = 0;
    /** Requires at least one digit character. */
    public static final int DIGIT = 1;
    /** Requires at least one alphabetic character. */
    public static final int ALPHA = 2;
    /** Requires an at-sign ({@code @}). */
    public static final int AT = 4;
    /** Requires a dot character ({@code .}). */
    public static final int DOT = 8;
    /** Requires a space character. */
    public static final int SPACE = 16;
    /** Requires a dash character ({@code -}). */
    public static final int DASH = 32;
    /** Requires a colon character ({@code :}). */
    public static final int COLUMN = 64;
    /** Requires a slash character ({@code /}). */
    public static final int SLASH = 128;

    private CharacterRequirements() {
    }
}
