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
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;

import java.io.Serializable;
import java.util.List;

public class KAnonymityMetric implements PrivacyMetric, Serializable {
    private long count = 0L;

    public long getCount() {
        return count;
    }

    public KAnonymityMetric() {
    }

    public KAnonymityMetric(long cnt) {
        this.count = cnt;
    }

    @Override
    public PrivacyMetric getInstance(List<String> sensitiveValues) {
        return new KAnonymityMetric(1);
    }

    @Override
    public void update(PrivacyMetric metric) {
        count += ((KAnonymityMetric) metric).getCount();
    }
}

