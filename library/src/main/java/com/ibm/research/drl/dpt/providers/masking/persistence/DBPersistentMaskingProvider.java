/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DBPersistentMaskingProvider extends AbstractPersistentMaskingProvider {
    private final DBCache cache;

    public DBPersistentMaskingProvider(MaskingProvider maskingProvider, MaskingConfiguration configuration) {
        super(maskingProvider, configuration);
        this.cache = new DBCache();
        String namespace = configuration.getStringValue("persistence.namespace");
        String connectionString = configuration.getStringValue("persistence.database.connectionString");
        String username = configuration.getStringValue("persistence.database.username");
        String password = configuration.getStringValue("persistence.database.password");
        int cacheLimit = configuration.getIntValue("persistence.database.cacheLimit");
        cache.initialize(connectionString, username, password, cacheLimit, namespace);
    }

    @Override
    protected boolean isCached(String value) {
        try {
            return null != cache.getValue(value);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to verify if the value exists in the cache");
        }
    }

    @Override
    protected String getCachedValue(String value) {
        try {
            return cache.getValue(value);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to retrieve value from cache");
        }
    }

    @Override
    protected void cacheValue(String value, String maskedValue) {
        try {
            cache.storeValue(value, maskedValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class DBCache {
        private int cacheLimit;
        private int cacheEntries = 0;
        private Connection connection;
        private String tableName;

        private Map<Integer, String> cache;

        public void initialize(String host, String username, String password, int cacheLimit, String namespace) {
            try {
                this.connection = DriverManager.getConnection(host, username, password);
                this.connection.setAutoCommit(true);
                this.cacheLimit = cacheLimit;
                this.tableName = "mappings_" + namespace;
                try {
                    initializeTable();
                } catch (SQLException ignored) {
                }
                initializeCache(cacheLimit);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error initializing DB cache");
            }
        }

        void shutDown() {
            try {
                this.connection.close();
            } catch (SQLException ignored) {
            }
        }

        private void initializeTable() throws SQLException {
            try (Statement statement = connection.createStatement();
                 ResultSet executionResult = statement.executeQuery("CREATE TABLE " + tableName + " (value text primary key, masked text)")
            ) {
            ;
            }
        }

        private void initializeCache(int cacheLimit) throws SQLException {
            this.cache = Collections.synchronizedMap(new HashMap<>());

            if (cacheLimit == 0) {
                return;
            }

            String SQL = "SELECT value, masked FROM " + tableName;

            if (cacheLimit != -1) {
                SQL += " LIMIT " + cacheLimit;
            }

            try (ResultSet resultSet = retrieveAllMappings(SQL);) {

                while (resultSet.next()) {
                    String value = resultSet.getString("value");
                    String masked = resultSet.getString("masked");

                    int valueCode = value.hashCode();
                    cache.put(valueCode, masked);

                    this.cacheEntries++;
                    if (cacheLimit != -1 && this.cacheEntries >= cacheLimit) {
                        break;
                    }
                }
            }
        }

        private ResultSet retrieveAllMappings(String SQL) throws SQLException {
            try Statement stmt = connection.createStatement();
            return stmt.executeQuery(SQL);
        }

        private ResultSet retrieveMapping(String value) throws SQLException {
            String SQL = "SELECT masked FROM " + tableName + " WHERE value = ?";
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setString(1, value);

            Statement stmt = connection.createStatement();
            return stmt.executeQuery(statement.toString());
        }

        private String getValueFromCache(String value) {
            if (cache.isEmpty()) {
                return null;
            }

            int valueCode = value.hashCode();

            return cache.get(valueCode);
        }

        public String getValue(String value) throws IOException, SQLException {
            if (!cache.isEmpty()) {
                String fromCache = getValueFromCache(value);
                if (fromCache != null) {
                    return fromCache;
                }
            }

            ResultSet rs = retrieveMapping(value);

            if (rs.next()) {
                return rs.getString("masked");
            }

            return null;
        }

        private String getValue(String value, MaskingProvider maskingProvider) throws IOException, SQLException {
            if (!cache.isEmpty()) {
                String fromCache = getValueFromCache(value);
                if (fromCache != null) {
                    return fromCache;
                }
            }

            ResultSet rs = retrieveMapping(value);

            if (rs.next()) {
                return rs.getString("masked");
            }

            String maskedValue = maskingProvider.mask(value);

            String query = " insert into " + tableName + "(value, masked) values (?, ?)";

            PreparedStatement preparedStmt = this.connection.prepareStatement(query);
            preparedStmt.setString(1, value);
            preparedStmt.setString(2, maskedValue);

            try {
                preparedStmt.execute();
            } catch (SQLIntegrityConstraintViolationException e) {
                rs = retrieveMapping(value);
                if (rs.next()) {
                    String finalMasked = rs.getString("masked");
                    putToCache(value, finalMasked);
                    return finalMasked;
                } else {
                    throw new RuntimeException("out of sync");
                }
            }

            putToCache(value, maskedValue);
            return maskedValue;
        }

        private void putToCache(String value, String maskedValue) {
            if (cacheLimit == 0) {
                return;
            }

            if (cacheLimit != -1 && this.cacheEntries >= cacheLimit) {
                return;
            }

            int valueCode = value.hashCode();
            cache.put(valueCode, maskedValue);
            this.cacheEntries++;
        }

        void storeValue(String value, String maskedValue) throws SQLException {
            try {
                String query = " insert into " + tableName + " (value, masked) values (?, ?)";

                PreparedStatement preparedStmt = this.connection.prepareStatement(query);
                preparedStmt.setString(1, value);
                preparedStmt.setString(2, maskedValue);


                preparedStmt.execute();
            } catch (SQLIntegrityConstraintViolationException e) {
                e.printStackTrace();
            }

            putToCache(value, maskedValue);
        }
    }
}
