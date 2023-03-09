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
package com.ibm.research.drl.dpt.models;

public class SSNUS {
    private final String areaNumber;
    private final String group;
    private final String serialNumber;

    /**
     * Instantiates a new Ssnus.
     *
     * @param areaNumber   the area number
     * @param group        the group
     * @param serialNumber the serial number
     */
    public SSNUS(String areaNumber, String group, String serialNumber) {
        this.areaNumber = areaNumber;
        this.group = group;
        this.serialNumber = serialNumber;
    }

    /**
     * Gets area number.
     *
     * @return the area number
     */
    public String getAreaNumber() {
        return areaNumber;
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        String builder = areaNumber + "-" +
                group +
                "-" +
                serialNumber;
        return builder;
    }
}
