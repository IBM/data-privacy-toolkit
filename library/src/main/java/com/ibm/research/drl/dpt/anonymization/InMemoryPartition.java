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

import java.util.List;

public class InMemoryPartition implements Partition {
    private final IPVDataset dataset;
    private boolean isAnon;

    @Override
    public int size() {
        return dataset.getNumberOfRows();
    }

    @Override
    public double getNormalizedWidth(int qidColumn) {
        return 0;
    }

    @Override
    public IPVDataset getMember() {
        return this.dataset;
    }

    @Override
    public boolean isAnonymous() {
        return isAnon;
    }

    @Override
    public void setAnonymous(boolean value) {
        this.isAnon = value;
    }

    /**
     * Instantiates a new Ola partition.
     *
     * @param values the values
     */
    public InMemoryPartition(List<List<String>> values) {
        this.dataset = new IPVDataset(
                values,
                null,
                false
        );
    }

    public InMemoryPartition(int numberOfColumns) {
        this.dataset = new IPVDataset(
                numberOfColumns);
    }

    public InMemoryPartition(IPVDataset dataset) {
        this.dataset = dataset;
    }
}
