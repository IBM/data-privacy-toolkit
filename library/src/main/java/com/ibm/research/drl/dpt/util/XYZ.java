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
package com.ibm.research.drl.dpt.util;

/** Represents a point or vector in three-dimensional space. */
public class XYZ {
    private final double x;
    private final double y;
    private final double z;

    /**
     * Returns the X component.
     *
     * @return the X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y component.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the Z component.
     *
     * @return the Z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Constructs an XYZ point with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public XYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
