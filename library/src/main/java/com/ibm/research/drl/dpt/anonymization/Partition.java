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
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;

public interface Partition {
    /**
     * Size int.
     *
     * @return the int
     */
    int size();

    /**
     * Gets normalized width.
     *
     * @param qidColumn the qid column
     * @return the normalized width
     */
    double getNormalizedWidth(int qidColumn);

    /**
     * Gets member.
     *
     * @return the member
     */
    IPVDataset getMember();

    boolean isAnonymous();

    void setAnonymous(boolean value);
}

