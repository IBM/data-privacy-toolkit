/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import com.ibm.research.drl.dpt.util.localization.ResourceEntryType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public final class IdentifierFactory implements Serializable {
    private static final Logger logger = LogManager.getLogger(IdentifierFactory.class);
    private static final IdentifierFactory DEFAULT_IDENTIFIER_FACTORY;

    static {
        try (InputStream inputStream = IdentifierFactory.class.getResourceAsStream("/identifiers.properties")) {
            DEFAULT_IDENTIFIER_FACTORY = new IdentifierFactory(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class IdentifierFactoryHelper implements Serializable {
        private final Set<Identifier> identifiers = Collections.synchronizedSet(new HashSet<>());

        @SuppressWarnings("unchecked")
        private IdentifierFactoryHelper(final InputStream is) {
            Properties properties = new Properties();
            if (Objects.nonNull(is)) {
                try {
                    properties.load(is);
                    for (Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); ) {
                        String name = (String) names.nextElement();

                        registerIdentifier((Class<? extends Identifier>) Class.forName(name));
                    }
                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("Unable to instantiate classes for {}", properties);
                    throw new RuntimeException(e);
                }
            } else {
                logger.info("No default list of identifiers provided");
            }
        }

        @SuppressWarnings("unchecked")
        private IdentifierFactoryHelper(final Set<String> names) {
            for(String name: names) {
                try {
                    registerIdentifier((Class<? extends Identifier>) Class.forName(name));
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    logger.error("Unable to initialize class {}", name);
                    throw new RuntimeException(e);
                }
            }
        }


        /**
         * Available identifiers collection.
         *
         * @return the collection
         */
        public Collection<Identifier> availableIdentifiers() {
            return identifiers;
        }

        /**
         * Register identifier.
         *
         * @param identifierType the identifier type
         * @throws IllegalAccessException the illegal access exception
         * @throws InstantiationException the instantiation exception
         */
        public void registerIdentifier(final Class<? extends Identifier> identifierType) throws IllegalAccessException, InstantiationException {
            try {
                registerIdentifier(identifierType.getConstructor().newInstance());
            } catch (InvocationTargetException | NoSuchMethodException e) {
                logger.error("Unable to register {}", identifierType.toString());
                throw new RuntimeException(e);
            }
        }

        /**
         * Register identifier.
         *
         * @param identifier the identifier
         */
        public void registerIdentifier(final Identifier identifier) {
            identifiers.add(identifier);
        }
    }

    private final IdentifierFactoryHelper helper;
    
    public static IdentifierFactory getDefaultIdentifierFactory() {
        return DEFAULT_IDENTIFIER_FACTORY;
    }
    
    public Collection<Identifier> availableIdentifiers() {
       return this.helper.availableIdentifiers(); 
    }

    public IdentifierFactory(InputStream configuration) {
        this.helper = new IdentifierFactoryHelper(configuration);
    }
    
    public IdentifierFactory(Set<String> identifierClassNames) {
        this.helper = new IdentifierFactoryHelper(identifierClassNames);
    }
    
    /**
     * Available identifiers collection.
     *
     * @return the collection
     */
    public static Collection<Identifier> defaultIdentifiers() {
        return getDefaultIdentifierFactory().availableIdentifiers();
    }

    public void registerIdentifier(final Class<? extends Identifier> identifier) throws InstantiationException, IllegalAccessException {
        this.helper.registerIdentifier(identifier);
    }

    public void registerIdentifier(final Identifier identifier) {
        this.helper.registerIdentifier(identifier);
    }

    public static IdentifierFactory initializeIdentifiers(JsonNode identifiers) {
        IdentifierFactory factory = new IdentifierFactory(Collections.emptySet());

        if (identifiers.isArray()) {
            identifiers.elements().forEachRemaining(
                    identifier -> {
                        if (null == identifier || identifier.isNull()) return;

                        if (identifier.isTextual()) {
                            try {
                                factory.registerIdentifier((Class<? extends Identifier>) Class.forName(identifier.asText()));
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                logger.error("Error initializing class", e);
                            }
                        } else if (identifier.isObject()) {
                            final PluggableIdentifierType type = PluggableIdentifierType.valueOf(identifier.get("type").asText());

                            switch (type) {
                                case DICTIONARY:
                                    factory.registerIdentifier(buildDictionaryBasedUserDefinedIdentifier(identifier));
                                    break;
                                case REGEX:
                                    factory.registerIdentifier(buildRegexBasedUserDefinedIdentifier(identifier));
                                    break;
                                default:
                                    throw new RuntimeException("Unknown type " + type);
                            }
                        } else {
                            throw new RuntimeException("Not supported node type");
                        }
                    }
            );
        }

        if (identifiers.isObject()) {
            JsonNode resourceTypeNode = identifiers.get("resourceType");
            if (resourceTypeNode == null) {
                throw new RuntimeException("Missing resourceType from identifiers");
            }

            if (identifiers.has("path")) {
                logger.info("Load identifiers from a path: " + identifiers.get("path").toString());
                readClassNamesFromFile(
                        identifiers.get("path").asText(), ResourceEntryType.valueOf(resourceTypeNode.asText())).forEach(
                        identifierClassName -> {
                            try {
                                factory.registerIdentifier((Class<? extends Identifier>) Class.forName(identifierClassName));
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                logger.error("Error initializing class", e);
                            }
                        });
            } else {
                if (!identifiers.has("paths")) {
                    throw new RuntimeException("Missing path from identifiers");
                }

                JsonNode pathsNode = identifiers.get("paths");

                pathsNode.elements().forEachRemaining(pathNode -> readClassNamesFromFile(
                        pathNode.asText(), ResourceEntryType.valueOf(resourceTypeNode.asText())).forEach(
                        identifierClassName -> {
                            try {
                                factory.registerIdentifier((Class<? extends Identifier>) Class.forName(identifierClassName));
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                logger.error("Error initializing class", e);
                            }
                        }
                ));
            }
        }

        return factory;
    }

    private static Set<String> readClassNamesFromFile(String path, ResourceEntryType resourceEntryType) {
        ResourceEntry resourceEntry = new ResourceEntry(
                path,
                "",
                resourceEntryType
        );

        try (InputStream resourceStream = resourceEntry.createStream()) {
            Properties properties = new Properties();
            properties.load(resourceStream);

            Set<String> identifiers = new HashSet<>(properties.size());
            for (Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); ) {
                String name = (String) names.nextElement();
                identifiers.add(name);
            }
            return identifiers;
        } catch (IOException e) {
            throw new RuntimeException("Unable to create resources", e);
        }
    }

    private static Identifier buildRegexBasedUserDefinedIdentifier(JsonNode spec) {
        final List<String> expressions = extractRegularExpressions(spec);

        boolean isPosIndependent = !spec.has("isPosIndependent") || spec.get("isPosIndependent").asBoolean();

        return new PluggableRegexIdentifier(
                spec.get("providerType").asText(),
                buildNameList(spec.get("names")),
                expressions,
                ValueClass.TEXT,
                isPosIndependent
        );
    }

    private static Collection<String> buildNameList(JsonNode specNames) {
        if (specNames != null && specNames.isArray()) {
            List<String> names = new ArrayList<>();

            specNames.elements().forEachRemaining( element -> {
                String v = element.asText("").trim();
                if (!v.isEmpty()) {
                    names.add(v);
                }
            });

            return names;
        }

        return Collections.emptyList();
    }

    private static List<String> extractRegularExpressions(JsonNode spec) {
        List<String> expressions = new ArrayList<>();

        if (spec.has("regex")) {
            spec.get("regex").elements().forEachRemaining(e -> {
                if (null != e && e.isTextual()) {
                    expressions.add(e.asText().trim());
                }
            });
        } else if (spec.has("paths")) {
            spec.get("paths").elements().forEachRemaining(
                    path -> expressions.addAll(readStringsFromFile(path.asText()))
            );
        } else {
            throw new RuntimeException("No specification available");
        }

        return expressions;
    }

    private static Identifier buildDictionaryBasedUserDefinedIdentifier(JsonNode spec) {
        boolean isPosIndependent = !spec.has("isPosIndependent") || spec.get("isPosIndependent").asBoolean();
        boolean ignoreCase = !spec.has("ignoreCase") || spec.get("ignoreCase").asBoolean();

        return new PluggableLookupIdentifier(
                spec.get("providerType").asText(),
                Collections.emptyList(),
                buildTermsList(spec),
                ignoreCase,
                ValueClass.TEXT,
                isPosIndependent
        );
    }

    private static Collection<String> buildTermsList(JsonNode spec) {
        Set<String> terms = new HashSet<>();

        if (spec.has("terms")) {
            spec.get("terms").elements().forEachRemaining( term -> terms.add(term.asText().trim()));
        }
        if (spec.has("paths")) {
            spec.get("paths").elements().forEachRemaining(
                    path -> terms.addAll(readStringsFromFile(path.asText()))
            );
        }
        if (terms.isEmpty()) throw new RuntimeException("No terms specified");

        return terms;
    }

    private static Collection<? extends String> readStringsFromFile(String path) {
        Set<String> terms = new HashSet<>();
        for (File file : populateFilesToProcess(path)) {
            try {
                terms.addAll(
                        Files.lines(file.toPath()).map(String::trim).collect(Collectors.toList())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return terms;
    }

    private static File[] populateFilesToProcess(String fileName) {
        final File fileToProcess = new File(fileName);

        if (!fileToProcess.exists()) throw new RuntimeException(fileToProcess.getAbsoluteFile() + " does not exist");

        if (fileToProcess.isDirectory()) {
            return Objects.requireNonNull(fileToProcess.listFiles());
        }   else {
            return new File[]{ fileToProcess };
        }
    }
}
