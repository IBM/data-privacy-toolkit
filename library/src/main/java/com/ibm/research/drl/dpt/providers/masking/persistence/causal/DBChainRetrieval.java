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
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBChainRetrieval implements ChainRetrieval {
    private final Connection connection;
    private final String tableName;
    private final List<DictionaryEntry> lastState;

    public DBChainRetrieval(String host, String username, String password, String tableName) {
        try {
            this.connection = DriverManager.getConnection(host, username, password);
            this.connection.setAutoCommit(true);
            this.tableName = tableName;
            this.lastState = new ArrayList<>();

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing DB cache", e);
        }
    }

    private ResultSet retrieveAllMappings(String SQL) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(SQL);
    }

    private int retrieveCount(String tableName) throws SQLException {
        String SQL = "SELECT count(*) FROM " + tableName;
        ResultSet rs = retrieveAllMappings(SQL);
        rs.next();
        return rs.getInt(1);
    }

    @Override
    public void append(String hashedTerm) throws SQLException {
        try (
            PreparedStatement updateChain = connection.prepareStatement("INSERT INTO " + tableName + "(value, type) VALUES(? , ?)")
        ) {
            updateChain.setString(1, hashedTerm);
            updateChain.setString(2, DictionaryEntryType.VALUE.toString());

            updateChain.executeUpdate();
        }
    }

    @Override
    public List<DictionaryEntry> retrieveChain() throws IOException {
        try {

            int currentCount = retrieveCount(tableName);
            if (currentCount == this.lastState.size()) {
                return this.lastState;
            }

            String SQL = "SELECT value, type FROM " + tableName + " WHERE idx >= " + this.lastState.size() + " ORDER BY idx";

            ResultSet resultSet = retrieveAllMappings(SQL);

            while (resultSet.next()) {
                String value = resultSet.getString("value");
                String type = resultSet.getString("type");
                DictionaryEntry e = new DictionaryEntry(value, DictionaryEntryType.valueOf(type));
                this.lastState.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return this.lastState;
    }

    @Override
    public void shutDown() {

    }
}
