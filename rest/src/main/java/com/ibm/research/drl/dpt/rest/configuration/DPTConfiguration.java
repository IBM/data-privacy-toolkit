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
package com.ibm.research.drl.dpt.rest.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.processors.CSVFormatProcessor;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

@Configuration
public class DPTConfiguration {
    public static final Logger logger = LogManager.getLogger(DPTConfiguration.class);

    @Bean
    public WebServerFactoryCustomizer<WebServerFactory> containerCustomizer() {
        return (WebServerFactory container) -> {
            if (container instanceof TomcatServletWebServerFactory) {
                TomcatServletWebServerFactory tomcat = (TomcatServletWebServerFactory) container;
                tomcat.addConnectorCustomizers(
                        connector -> connector.setMaxPostSize(20971520) // 20 MiB max post size
                );
            }
        };
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jsonConverter.getObjectMapper();
        SimpleModule module = new SimpleModule("Stream");
        module.addSerializer(Stream.class, new JsonSerializer<>() {
            @Override
            public void serialize(Stream value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                serializers.
                        findValueSerializer(Iterator.class, null).serialize(value.iterator(), gen, serializers);
            }
        });

        objectMapper.registerModule(module);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

    @Bean
    public CSVFormatProcessor createCsvFormatProcessor() {
        logger.info("Creating a CSV format Processor");

        return new CSVFormatProcessor();
    }

    @Bean
    public DefaultMaskingConfiguration createMaskingConfiguration() {
        logger.info("Creating masking configuration");

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();

        logger.info("Setting demo-specific options: replace.mask.offset = 0");

        configuration.setValue("replace.mask.offset", 0);

        logger.info("Setting demo-specific options: replace.mask.preserve = 4");

        configuration.setValue("replace.mask.preserve", 4);

        logger.info("Setting demo-specific options: binning.mask.binSize = 10");

        configuration.setValue("binning.mask.binSize", 10);

        logger.info("Setting demo-specific options: binning.mask.returnBinMean = true");

        configuration.setValue("binning.mask.returnBinMean", true);

        return configuration;
    }

    @Bean
    public MaskingProviderFactory createMaskingProviderFactory(@Autowired DefaultMaskingConfiguration configuration) {
        logger.info("Creating default MaskingProviderFactory");

        return new MaskingProviderFactory(
                new ConfigurationManager(configuration),
                Collections.emptyMap());
    }

    @Bean
    public IdentifierFactory createIdentifierFactory() throws IOException {
        try (InputStream stream = DPTConfiguration.class.getResourceAsStream("/identifiers.properties")) {
            return new IdentifierFactory(stream);
        }
    }
}
