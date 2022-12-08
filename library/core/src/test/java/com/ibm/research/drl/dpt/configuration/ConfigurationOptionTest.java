/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConfigurationOptionTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void writesObject() throws IOException {
        ConfigurationOption option = new ConfigurationOption(
                mapper.createArrayNode().add("foo"),
                "description",
                "category");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(option);

        oos.close();
    }

    @Test
    public void writesNullValues() throws IOException {
        ConfigurationOption option = new ConfigurationOption(
                mapper.createArrayNode().add("foo"),
                null,
                "category");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(option);
    }

    @Test
    public void readsObject() throws IOException, ClassNotFoundException {
        ConfigurationOption option = new ConfigurationOption(
                mapper.createArrayNode().add("foo"),
                "description",
                "category");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(option);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            ConfigurationOption read = (ConfigurationOption) ois.readObject();

            assertThat(read.getCategory(), is(option.getCategory()));
            assertThat(read.getDescription(), is(option.getDescription()));
            assertThat(read.getValue(), is(option.getValue()));
        }
    }

    @Test
    public void readsNullValues() throws IOException, ClassNotFoundException {
        ConfigurationOption option = new ConfigurationOption(
                mapper.createArrayNode().add("foo"),
                null,
                "category");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(option);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            ConfigurationOption read = (ConfigurationOption) ois.readObject();

            assertThat(read.getCategory(), is(option.getCategory()));
            assertThat(read.getDescription(), is(option.getDescription()));
            assertThat(read.getValue(), is(option.getValue()));
        }
    }

    @Test
    public void readsPrimitiveTypes() throws IOException, ClassNotFoundException {
        ConfigurationOption option = new ConfigurationOption(
                null,
                null,
                "category");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(option);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            ConfigurationOption read = (ConfigurationOption) ois.readObject();

            assertThat(read.getCategory(), is(option.getCategory()));
            assertThat(read.getDescription(), is(option.getDescription()));
            assertThat(read.getValue(), is(option.getValue()));
        }
    }
}