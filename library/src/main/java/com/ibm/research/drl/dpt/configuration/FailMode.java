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
package com.ibm.research.drl.dpt.configuration;


/** Constants representing the behaviour when masking fails. */
public class FailMode {
    /** Not instantiable. */
    private FailMode() {}

    /** Return the original value on failure. */
    public static final int RETURN_ORIGINAL = 1;
    /** Return an empty string on failure. */
    public static final int RETURN_EMPTY = 2;
    /** Throw an error on failure. */
    public static final int THROW_ERROR = 3;
    /** Generate a random value on failure. */
    public static final int GENERATE_RANDOM = 4;
}
