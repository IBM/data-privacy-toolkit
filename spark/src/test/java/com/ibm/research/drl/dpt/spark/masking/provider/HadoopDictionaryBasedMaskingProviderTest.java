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
package com.ibm.research.drl.dpt.spark.masking.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class HadoopDictionaryBasedMaskingProviderTest {
    @Test
    public void readSingleDictionaryFile() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("hadoop.dictionary.path", HadoopDictionaryBasedMaskingProviderTest.class.getResource("/test1.txt").getFile());

        HadoopDictionaryBasedMaskingProvider provider = new HadoopDictionaryBasedMaskingProvider(new SecureRandom(), configuration);
        
        assertThat(provider.mask("bar"), is("foo"));
    }

    @Test
    @Disabled("Require porting back handling of multiple files")
    public void readMultiplesDictionaryFile() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("hadoop.dictionary.path",
                JsonUtils.MAPPER.createArrayNode().
                        add(HadoopDictionaryBasedMaskingProviderTest.class.getResource("/test1.txt").getFile()).
                        add(HadoopDictionaryBasedMaskingProviderTest.class.getResource("/test2.txt").getFile())
        );

        HadoopDictionaryBasedMaskingProvider provider = new HadoopDictionaryBasedMaskingProvider(new SecureRandom(), configuration);

        assertThat(provider.mask("bear"), anyOf(is("foo"), is("bar")));
    }
}