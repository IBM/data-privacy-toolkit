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
package com.ibm.research.drl.dpt.anonymization.hierarchies;


import java.io.Serializable;
import java.util.Set;


/**
 * Interface for generalization hierarchies used in categorical anonymization.
 */
public interface GeneralizationHierarchy extends Serializable {
    /**
     * Returns the height (number of levels) of this hierarchy.
     *
     * @return the hierarchy height
     */
    int getHeight();

    /**
     * Returns the total number of leaf nodes in this hierarchy.
     *
     * @return the total leaf count
     */
    long getTotalLeaves();

    /**
     * Returns the number of leaves reachable from the given node.
     *
     * @param value the node value
     * @return the leaf count for the node
     */
    int leavesForNode(String value);

    /**
     * Returns the set of leaf values for the given node.
     *
     * @param value the node value
     * @return the set of leaf values
     */
    Set<String> getNodeLeaves(String value);

    /**
     * Returns the hierarchy level of the given node (0 for leaves).
     *
     * @param value the node value
     * @return the node level, or -1 if not found
     */
    int getNodeLevel(String value);

    /**
     * Returns the top-level (most general) term of this hierarchy.
     *
     * @return the top term
     */
    String getTopTerm();

    /**
     * Encodes a leaf value at the given generalization level.
     *
     * @param value            the leaf value to encode
     * @param level            the number of levels to generalize up
     * @param randomizeOnFail  whether to return a random value if encoding fails
     * @return the generalized value
     */
    String encode(String value, int level, boolean randomizeOnFail);
}
