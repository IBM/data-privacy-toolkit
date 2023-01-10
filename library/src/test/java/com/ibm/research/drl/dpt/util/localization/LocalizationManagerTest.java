/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util.localization;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

public class LocalizationManagerTest {
    private final LocalizationManager manager = LocalizationManager.getInstance();

    @Test
    public void initialization() {
        assertTrue(true);
    }

    @Test
    public void getAllResources() {
        for (Resource resource : Resource.values()) {
            Collection<ResourceEntry> resources = manager.getResources(resource);

            assertNotNull(resources);
            assertThat(resources.size(), is(not(0)));
        }
    }

    @Test
    public void getResourcesUsIsEn() {
        Collection<ResourceEntry> resources = manager.getResources(Resource.COUNTRY, Collections.singleton("us"));

        assertNotNull(resources);
        assertThat(resources.size(), is(1));

        for (ResourceEntry resourceEntry : resources)
            assertThat(resourceEntry.getCountryCode(), is("en"));
    }

    @Test
    public void getResourcesUkIsEn() {
        Collection<ResourceEntry> resources = manager.getResources(Resource.COUNTRY, Collections.singleton("uk"));

        assertNotNull(resources);
        assertThat(resources.size(), is(1));

        for (ResourceEntry resourceEntry : resources)
            assertThat(resourceEntry.getCountryCode(), is("en"));
    }

    @Test
    public void getResourcesWithCountry() {
        for (Resource resource : Resource.values()) {
            Collection<ResourceEntry> resources = manager.getResources(resource, Collections.singleton("us"));

            assertNotNull(resources);
        }
    }

    @Test
    public void userCanRegisterANewCountry() {
        manager.registerCountryCode("test");
    }

    @Test
    public void userCannotRegisterAResourceForUnknownCountryCode() {
        assertThrows(IllegalArgumentException.class, () -> manager.registerResource(Resource.CITY, "test123", "something something"));
    }

    @Test
    public void userCanRegisterNewExternalResources() {
        String filePath = Objects.requireNonNull(LocalizationManagerTest.class.getResource("/identifier/races_fantasy.csv")).getPath();
        String newCountryCode = "fantasy";

        manager.registerCountryCode(newCountryCode);

        manager.registerResource(Resource.RACE_ETHNICITY, newCountryCode, filePath);

        Collection<ResourceEntry> resources = LocalizationManager.getInstance().getResources(Resource.RACE_ETHNICITY, Collections.singleton("fantasy"));

        assertNotNull(resources);
        assertThat(resources.size(), is(1));

        ResourceEntry entry = resources.iterator().next();
        assertEquals(entry.getCountryCode(), newCountryCode);
    }
}
