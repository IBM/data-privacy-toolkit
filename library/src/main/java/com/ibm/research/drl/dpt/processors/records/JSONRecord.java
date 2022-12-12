/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors.records;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.ibm.research.drl.jsonpath.JSONPathException;
import com.ibm.research.drl.jsonpath.JSONPathExtractor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class JSONRecord extends MultipathRecord {
    private static final Logger logger = LogManager.getLogger(JSONRecord.class);
    private final JsonNode node;

    public JSONRecord(JsonNode node) {
        this.node = node;
    }

    @Override
    public byte[] getFieldValue(String fieldReference) {
        JsonNode node;
        try {
            node = JSONPathExtractor.extract(this.node, fieldReference);
            if (node.isNull()) {
                logger.debug("Field reference {} points to null", fieldReference);
                return null;
            }
            if (node.isArray()) {
                logger.debug("Field reference {} points to array", fieldReference);
                return null;
            }
            if (node.isObject()) {
                logger.debug("Field reference {} points to object", fieldReference);
                return null;
            }
        } catch (JSONPathException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
        return node.asText().getBytes();
    }

    @Override
    public void setFieldValue(String fieldReference, byte[] value) {
        try {
            JsonNode originalNode = JSONPathExtractor.extract(node, fieldReference);

            JsonNode updatedValue = createUpdatedValue(originalNode, value);

            JSONPathExtractor.update(node, fieldReference, updatedValue);
        } catch (JSONPathException e) {
            e.printStackTrace();
        }
    }

    public JsonNode getNode() {
        return node;
    }

    public Iterable<String> generatePaths(String pattern) {
        List<String> pointers = Arrays.asList(pattern.split("/"));

        return generatePaths(node, pointers.subList(1, pointers.size()), "");
    }

    private List<String> generatePaths(JsonNode node, List<String> parts, String head) {
        if (node == null) { return Collections.emptyList(); }

        if (parts.isEmpty()) {
            return Collections.singletonList(head);
        }

        String part = parts.get(0);

        if (!part.equals("*")) {
            if (node.isObject() || node.isNull()) {
                return generatePaths(node.get(part), parts.subList(1, parts.size()), head + "/" + part);
            } else if (node.isArray()) {
                return generatePaths(node.get(Integer.parseInt(part)), parts.subList(1, parts.size()), head + "/" + part);
            } else {
                throw new IllegalArgumentException("Cannot access field " + part + " of a value node");
            }
        }

        if (!node.isArray()) throw new UnsupportedOperationException("Unable to iterate over not array");

        List<String> paths = new ArrayList<>();

        for (int i = 0; i < node.size(); ++i) {
            paths.addAll(generatePaths(node.get(i), parts.subList(1, parts.size()), head + "/" + i));
        }

        return paths;
    }

    private JsonNode createUpdatedValue(JsonNode originalNode, byte[] value) {
        if (null == value) {
            return NullNode.getInstance();
        }
        if (0 == value.length) {
            return new TextNode("");
        }
        try {
            switch (originalNode.getNodeType()) {
                case STRING:
                case MISSING:
                    return new TextNode(new String(value));
                case BINARY:
                    return new BinaryNode(value);
                case BOOLEAN:
                    return BooleanNode.valueOf(myParseBoolean(new String(value)));
                case NULL:
                    return originalNode;
                case NUMBER:
                    NumericNode numberValue = (NumericNode) originalNode;

                    if (numberValue.isShort()) return new ShortNode(Short.parseShort(new String(value)));
                    if (numberValue.isLong()) return new LongNode(Long.parseLong(new String(value)));
                    if (numberValue.isInt()) return new IntNode(Integer.parseInt(new String(value)));
                    if (numberValue.isFloat()) return new FloatNode(Float.parseFloat(new String(value)));
                    if (numberValue.isDouble()) return new DoubleNode(Double.parseDouble(new String(value)));

                case ARRAY:
                case OBJECT:
                case POJO:
                default:
                    logger.debug("Default behavior applied");
                    return new TextNode(new String(value));
            }
        } catch (IllegalArgumentException e) {
            logger.debug("Incompatible type");
            return new TextNode(new String(value));
        }
    }

    private boolean myParseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Neither true or false");
    }

    @Override
    public Iterable<String> getFieldReferences() {
        return getPaths(node, "");
    }

    @Override
    public Iterable<String> getFieldReferencesWithGeneralization() {
        return listAllLeaves(this.node, "");
    }

    private Iterable<String> listAllLeaves(JsonNode node, String pathSoFar) {
        JsonNodeType nodeType = node.getNodeType();

        switch (nodeType) {
            case ARRAY:
                return () -> IntStream.range(0, node.size()).boxed().flatMap( position -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                            listAllLeaves(
                                    node.get(position),
                                    pathSoFar + "/*"
                            ).iterator(),
                                Spliterator.IMMUTABLE
                        ), false)).distinct().iterator();
            case OBJECT:
                return () -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                node.fieldNames(),
                                Spliterator.IMMUTABLE
                        ), false
                ).flatMap(fieldName -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                listAllLeaves(node.get(fieldName), pathSoFar + '/' + fieldName).iterator(),
                                Spliterator.IMMUTABLE
                        ), false)).iterator();
            default:
                return Collections.singletonList(pathSoFar);
        }
    }

    private Iterable<String> getPaths(JsonNode node, String parent) {
        JsonNodeType nodeType = node.getNodeType();
        if (nodeType == JsonNodeType.ARRAY) {
            return () -> IntStream.range(0, node.size()).boxed().flatMap(position -> StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            getPaths(node.get(position), parent + "/" + position).iterator(),
                            Spliterator.IMMUTABLE
                    ), false)).iterator();
        } else if (nodeType == JsonNodeType.OBJECT) {
            return () -> StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            node.fieldNames(),
                            Spliterator.IMMUTABLE
                    ), false
            ).flatMap(fieldName -> StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            getPaths(node.get(fieldName), parent + '/' + fieldName).iterator(),
                            Spliterator.IMMUTABLE
                    ), false)).iterator();
        }
        return Collections.singletonList(parent);
    }

    @Override
    protected String formatRecord() {
        return node.toString();
    }

    public String getBasepath(String path) {
        int idx = path.lastIndexOf('/');
        return path.substring(0, idx + 1);
    }

    public boolean isAbsolute(String fieldName) {
        return fieldName.startsWith("/");
    }

    public boolean isSingleElement(String fieldIdentifier) {
        return ! fieldIdentifier.contains("*");
    }

    @Override
    public boolean isPrimitiveType(String fieldIdentifier) {
        try {
            JsonNode fieldNode = JSONPathExtractor.extract(this.node, fieldIdentifier);
            switch (fieldNode.getNodeType()) {
                case BINARY:
                case BOOLEAN:
                case NUMBER:
                case STRING:
                case MISSING:
                case NULL:
                    return true;
            }
        } catch (JSONPathException e) {
            logger.debug("Error extracting", e);
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public Object getFieldObject(String fieldIdentifier) {
        try {
            return JSONPathExtractor.extract(this.node, fieldIdentifier);
        } catch (JSONPathException e) {
            logger.debug("Error extracting", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void suppressField(String fieldReference) {

        try {
            JSONPathExtractor.remove(node, fieldReference);
        } catch (JSONPathException e) {
            throw new IllegalArgumentException("The input schema does not contain the field marked as to be suppressed.");
        }

    }
}
