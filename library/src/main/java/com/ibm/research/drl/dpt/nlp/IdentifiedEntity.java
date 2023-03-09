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


public final class IdentifiedEntity implements Serializable {
    private final String text;
    private final int start;
    private final int end;
    private final Set<IdentifiedEntityType> type;
    private final Set<PartOfSpeechType> pos;
    private DependencyParseInformation dependencyParseInformation; 
    
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

    public DependencyParseInformation getDependencyParseInformation() {
        return dependencyParseInformation;
    }

    public void setDependencyParseInformation(DependencyParseInformation dependencyParseInformation) {
        this.dependencyParseInformation = dependencyParseInformation;
    }
    
    public String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Set<IdentifiedEntityType> getType(){
        return type;
    }

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

    public String concatSources(String sep) {
        List<String> sources = new ArrayList<>();
        type.forEach(x -> sources.add(x.getSource()));
        return StringUtils.join(sources, sep);
    }
    
    public String concatTypes(String sep) {
        return StringUtils.join(type, sep);
    }
    
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
