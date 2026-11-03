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
package com.ibm.research.drl.dpt.schema;

/** The type of relationship between fields in a masking rule. */
public enum RelationshipType {
    /** Sum relationship. */
    SUM,
    /** Approximate sum relationship. */
    SUM_APPROXIMATE,
    /** Product relationship. */
    PRODUCT,
    /** Equality relationship. */
    EQUALS,
    /** Greater-than relationship. */
    GREATER,
    /** Distance relationship. */
    DISTANCE,
    /** Less-than relationship. */
    LESS,
    /** Linked relationship. */
    LINKED,
    /** Key relationship. */
    KEY,
    /** Ratio relationship. */
    RATIO,
    /** Grep-and-mask relationship for free text. */
    GREP_AND_MASK
}
