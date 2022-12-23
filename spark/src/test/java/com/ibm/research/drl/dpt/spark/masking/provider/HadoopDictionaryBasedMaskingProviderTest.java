/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
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