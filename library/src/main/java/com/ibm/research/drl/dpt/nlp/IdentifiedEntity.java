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
package com.ibm.research.drl.dpt.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/** Represents an entity identified in free text, including its position and type. */
public final class IdentifiedEntity implements Serializable {
    /** The text of the identified entity. */
    private final String text;
    /** The start offset (inclusive) in the original text. */
    private final int start;
    /** The end offset (exclusive) in the original text. */
    private final int end;
    /** The set of entity types assigned to this entity. */
    private final Set<IdentifiedEntityType> type;
    /** The set of part-of-speech tags assigned to this entity. */
    private final Set<PartOfSpeechType> pos;
    /** Dependency-parse information, if available. */
    private DependencyParseInformation dependencyParseInformation;

    /**
     * Constructs an IdentifiedEntity.
     *
     * @param text  the identified text span
     * @param start the start offset (inclusive) in the source text
     * @param end   the end offset (exclusive) in the source text
     * @param type  the set of entity types assigned to this entity
     * @param pos   the set of part-of-speech tags assigned to this entity
     */
    @JsonCreator
    public IdentifiedEntity(@JsonProperty("text") String text, @JsonProperty("start") int start, @JsonProperty("end") int end,
                            @JsonProperty("type") Set<IdentifiedEntityType> type,
                            @JsonProperty("pos") Set<PartOfSpeechType> pos) 
    {
        this.text = text;
        this.start = start;
        this.end = end;
        this.type = type;
        this.pos = pos;
        this.dependencyParseInformation = null;
    }

    /**
     * Returns the dependency parse information for this entity.
     *
     * @return the dependency parse information, or {@code null} if not set
     */
    public DependencyParseInformation getDependencyParseInformation() {
        return dependencyParseInformation;
    }

    /**
     * Sets the dependency parse information for this entity.
     *
     * @param dependencyParseInformation the dependency parse information
     */
    public void setDependencyParseInformation(DependencyParseInformation dependencyParseInformation) {
        this.dependencyParseInformation = dependencyParseInformation;
    }

    /**
     * Returns the text span of this entity.
     *
     * @return the entity text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the start offset of this entity in the source text.
     *
     * @return start offset (inclusive)
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end offset of this entity in the source text.
     *
     * @return end offset (exclusive)
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the set of entity types assigned to this entity.
     *
     * @return entity types
     */
    public Set<IdentifiedEntityType> getType() {
        return type;
    }

    /**
     * Returns the set of part-of-speech tags assigned to this entity.
     *
     * @return part-of-speech tags
     */
    @JsonIgnore
    public Set<PartOfSpeechType> getPos() {
        return pos;
    }

    @Override
    public String toString() {
        String repr = "IdentifiedEntity{" +
                "text='" + text + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", type=" + concatTypes("-") +
                ", sources=" + concatSources("-") +
                ", pos=" + pos;
        
        if (this.dependencyParseInformation != null) {
            repr += ", nmod=" + this.dependencyParseInformation.getNmod();
            repr += ", case=" + this.dependencyParseInformation.getCase();
        }
        
        repr += "}";
        return repr;
    }

    /**
     * Concatenates the source strings of all entity types with the given separator.
     *
     * @param sep the separator
     * @return concatenated sources
     */
    public String concatSources(String sep) {
        List<String> sources = new ArrayList<>();
        type.forEach(x -> sources.add(x.getSource()));
        return StringUtils.join(sources, sep);
    }
    
    /**
     * Concatenates the type representations with the given separator.
     *
     * @param sep the separator
     * @return concatenated type strings
     */
    public String concatTypes(String sep) {
        return StringUtils.join(type, sep);
    }
    
    /**
     * Returns an inline XML representation of this entity.
     *
     * @return inline XML string
     */
    public String toInlineXML() {
        if (null == type || type.isEmpty()) {
            return text;
        } else {
            
            if (type.size() == 1) {
                String typeName = type.iterator().next().getType();
                if (typeName.equals("UNKNOWN") || typeName.equals("O")) {
                    return text;
                }
            }
            
            return "<ProviderType:" + concatTypes(",") + ">" + text + "</ProviderType>";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiedEntity entity = (IdentifiedEntity) o;
        return start == entity.start &&
                end == entity.end &&
                Objects.equals(text, entity.text) &&
                Objects.equals(type, entity.type) &&
                Objects.equals(pos, entity.pos) &&
                Objects.equals(dependencyParseInformation, entity.dependencyParseInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, start, end, type, pos, dependencyParseInformation);
    }
}
