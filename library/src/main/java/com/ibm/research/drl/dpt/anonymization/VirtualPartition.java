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

public class VirtualPartition implements Partition {
    private final int size;
    private boolean anonymous;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public double getNormalizedWidth(int qidColumn) {
        return 0;
    }

    @Override
    public IPVDataset getMember() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return anonymous;
    }

    @Override
    public void setAnonymous(boolean value) {
        this.anonymous = value;
    }

    public VirtualPartition(int size) {
        this.size = size;
    }
}

