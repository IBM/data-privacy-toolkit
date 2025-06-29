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
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.ArrayList;
import java.util.List;

public class LDiversityMetric extends KAnonymityMetric {
    private final List<Histogram<String>> histograms;

    public List<Histogram<String>> getHistograms() {
        return histograms;
    }

    public LDiversityMetric() {
        super();
        this.histograms = new ArrayList<>();
    }

    public LDiversityMetric(List<String> sensitiveValues) {
        super(1);
        this.histograms = new ArrayList<>();

        for (String sensitiveValue : sensitiveValues) {
            Histogram<String> histogram = new Histogram<String>();
            histogram.put(sensitiveValue, 1L);

            this.histograms.add(histogram);
        }
    }

    @Override
    public PrivacyMetric getInstance(List<String> sensitiveValues) {
        return new LDiversityMetric(sensitiveValues);
    }

    @Override
    public void update(PrivacyMetric metric) {
        super.update(metric);

        LDiversityMetric lDiversityMetric = (LDiversityMetric) metric;
        List<Histogram<String>> metricHistograms = lDiversityMetric.getHistograms();

        for (int i = 0; i < metricHistograms.size(); i++) {
            if (histograms.size() <= i) {
                histograms.add(new Histogram<String>());
            }

            histograms.get(i).update(metricHistograms.get(i));
        }
    }
}
