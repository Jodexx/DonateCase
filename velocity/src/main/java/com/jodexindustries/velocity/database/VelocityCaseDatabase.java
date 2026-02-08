package com.jodexindustries.velocity.database;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class VelocityCaseDatabase {

    private static final String CACHE_ALL_KEY = "all!";
    private static final String THREAD_NAME_PREFIX = "donatecase-db";

    private final VelocityDatabase database;
    private final Logger logger;
    private final ExecutorService executor;
    private final VelocitySimpleCache<String, List<VelocityHistory>> cache;

    public VelocityCaseDatabase(VelocityDatabase database, Logger logger, long cacheMaxAgeTicks) {
        this.database = database;
        this.logger = logger;
        this.cache = new VelocitySimpleCache<>(cacheMaxAgeTicks);
        this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(THREAD_NAME_PREFIX));
    }

    public CompletableFuture<Map<String, Integer>> getKeys(String player) {
        return supplyAsync(() -> {
            Map<String, Integer> keys = new HashMap<>();
            String sql = "SELECT case_name, keys FROM player_keys WHERE player = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, player);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        keys.put(rs.getString("case_name"), rs.getInt("keys"));
                    }
                }
            } catch (SQLException e) {
                warning(e);
            }
            return keys;
        });
    }

    public CompletableFuture<Integer> getKeys(String name, String player) {
        return supplyAsync(() -> {
            String sql = "SELECT keys FROM player_keys WHERE player = ? AND case_name = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, player);
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("keys");
                    }
                }
            } catch (SQLException e) {
                warning(e);
            }
            return 0;
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> setKeys(String name, String player, int keys) {
        return supplyAsync(() -> {
            String updateSql = "UPDATE player_keys SET keys = ? WHERE player = ? AND case_name = ?";
            String insertSql = "INSERT INTO player_keys (player, case_name, keys) VALUES (?, ?, ?)";
            try (PreparedStatement update = requireConnection().prepareStatement(updateSql)) {
                update.setInt(1, keys);
                update.setString(2, player);
                update.setString(3, name);
                int updated = update.executeUpdate();
                if (updated == 0) {
                    try (PreparedStatement insert = requireConnection().prepareStatement(insertSql)) {
                        insert.setString(1, player);
                        insert.setString(2, name);
                        insert.setInt(3, keys);
                        insert.executeUpdate();
                    }
                }
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> setKeysBulk(String caseName, Map<String, Integer> playerKeysMap) {
        return supplyAsync(() -> {
            String updateSql = "UPDATE player_keys SET keys = ? WHERE player = ? AND case_name = ?";
            String insertSql = "INSERT INTO player_keys (player, case_name, keys) VALUES (?, ?, ?)";
            Connection conn = null;
            Boolean autoCommit = null;
            try {
                conn = requireConnection();
                autoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);

                try (PreparedStatement update = conn.prepareStatement(updateSql);
                     PreparedStatement insert = conn.prepareStatement(insertSql)) {

                    for (Map.Entry<String, Integer> entry : playerKeysMap.entrySet()) {
                        String player = entry.getKey();
                        int keys = entry.getValue();

                        update.setInt(1, keys);
                        update.setString(2, player);
                        update.setString(3, caseName);
                        int updated = update.executeUpdate();

                        if (updated == 0) {
                            insert.setString(1, player);
                            insert.setString(2, caseName);
                            insert.setInt(3, keys);
                            insert.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ignored) {
                        // ignore rollback errors
                    }
                }
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            } finally {
                if (conn != null && autoCommit != null) {
                    try {
                        conn.setAutoCommit(autoCommit);
                    } catch (SQLException ignored) {
                        // ignore auto-commit restore errors
                    }
                }
            }
        });
    }

    public CompletableFuture<Integer> getOpenCount(String player, String caseType) {
        return supplyAsync(() -> {
            String countColumn = countColumn();
            String sql = "SELECT COALESCE(SUM(" + countColumn + "), 0) AS total FROM open_info WHERE player = ? AND case_type = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, player);
                ps.setString(2, caseType);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total");
                    }
                }
            } catch (SQLException e) {
                warning(e);
            }
            return 0;
        });
    }

    public CompletableFuture<Map<String, Integer>> getOpenCount(String player) {
        return supplyAsync(() -> {
            Map<String, Integer> opens = new HashMap<>();
            String countColumn = countColumn();
            String sql = "SELECT case_type, SUM(" + countColumn + ") AS total FROM open_info WHERE player = ? GROUP BY case_type";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, player);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        opens.put(rs.getString("case_type"), rs.getInt("total"));
                    }
                }
            } catch (SQLException e) {
                warning(e);
            }
            return opens;
        });
    }

    public CompletableFuture<Map<String, Map<String, Integer>>> getGlobalOpenCount() {
        return supplyAsync(() -> {
            Map<String, Map<String, Integer>> globalMap = new HashMap<>();
            String countColumn = countColumn();
            String sql = "SELECT player, case_type, SUM(" + countColumn + ") AS total FROM open_info GROUP BY player, case_type";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String player = rs.getString("player");
                    String caseType = rs.getString("case_type");
                    int total = rs.getInt("total");
                    globalMap.computeIfAbsent(player, k -> new HashMap<>()).put(caseType, total);
                }
            } catch (SQLException e) {
                warning(e);
            }
            return globalMap;
        });
    }

    public CompletableFuture<Map<String, Integer>> getGlobalOpenCount(String caseType) {
        return supplyAsync(() -> {
            Map<String, Integer> opens = new HashMap<>();
            String countColumn = countColumn();
            String sql = "SELECT player, SUM(" + countColumn + ") AS total FROM open_info WHERE case_type = ? GROUP BY player";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, caseType);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        opens.put(rs.getString("player"), rs.getInt("total"));
                    }
                }
            } catch (SQLException e) {
                warning(e);
            }
            return opens;
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> setCount(String caseType, String player, int count) {
        return supplyAsync(() -> {
            String countColumn = countColumn();
            String updateSql = "UPDATE open_info SET " + countColumn + " = ? WHERE player = ? AND case_type = ?";
            String insertSql = "INSERT INTO open_info (player, case_type, " + countColumn + ") VALUES (?, ?, ?)";
            try (PreparedStatement update = requireConnection().prepareStatement(updateSql)) {
                update.setInt(1, count);
                update.setString(2, player);
                update.setString(3, caseType);
                int updated = update.executeUpdate();
                if (updated == 0) {
                    try (PreparedStatement insert = requireConnection().prepareStatement(insertSql)) {
                        insert.setString(1, player);
                        insert.setString(2, caseType);
                        insert.setInt(3, count);
                        insert.executeUpdate();
                    }
                }
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> addHistory(String caseType, VelocityHistory newEntry, int maxSize) {
        return supplyAsync(() -> {
            String countSql = "SELECT COUNT(*) AS total FROM history_data WHERE case_type = ?";
            String oldestSql = "SELECT time FROM history_data WHERE case_type = ? ORDER BY time ASC LIMIT 1";
            String deleteSql = "DELETE FROM history_data WHERE case_type = ? AND time = ?";

            try (PreparedStatement countStmt = requireConnection().prepareStatement(countSql)) {
                countStmt.setString(1, caseType);
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("total") >= maxSize) {
                        try (PreparedStatement oldestStmt = requireConnection().prepareStatement(oldestSql)) {
                            oldestStmt.setString(1, caseType);
                            try (ResultSet oldest = oldestStmt.executeQuery()) {
                                if (oldest.next()) {
                                    long time = oldest.getLong("time");
                                    try (PreparedStatement deleteStmt = requireConnection().prepareStatement(deleteSql)) {
                                        deleteStmt.setString(1, caseType);
                                        deleteStmt.setLong(2, time);
                                        deleteStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }

                VelocityHistory entry = newEntry;
                if (entry.caseType() == null || entry.caseType().isBlank()) {
                    entry = new VelocityHistory(
                            entry.id(),
                            entry.item(),
                            entry.playerName(),
                            entry.time(),
                            entry.group(),
                            caseType,
                            entry.action()
                    );
                }

                insertHistory(entry);
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> setHistoryData(String caseType, int index, VelocityHistory data) {
        return supplyAsync(() -> {
            try {
                List<VelocityHistory> results = getHistoryDataInternal(caseType, true);
                if (index < 0 || index >= results.size()) {
                    return VelocityDatabaseStatus.FAIL;
                }

                VelocityHistory existing = results.get(index);
                if (existing == null) {
                    insertHistory(data);
                } else {
                    updateHistory(existing.time(), caseType, data);
                }
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> removeHistoryData(String caseType) {
        return supplyAsync(() -> {
            String sql = "DELETE FROM history_data WHERE case_type = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, caseType);
                ps.executeUpdate();
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> removeHistoryData(String caseType, int index) {
        return supplyAsync(() -> {
            String sql = "DELETE FROM history_data WHERE case_type = ? AND id = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, caseType);
                ps.setInt(2, index);
                ps.executeUpdate();
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<List<VelocityHistory>> getHistoryData() {
        return supplyAsync(() -> {
            try {
                return getHistoryDataInternal(null, false);
            } catch (SQLException e) {
                warning(e);
                return new ArrayList<>();
            }
        });
    }

    public CompletableFuture<List<VelocityHistory>> getHistoryData(String caseType) {
        return supplyAsync(() -> {
            try {
                return getHistoryDataInternal(caseType, true);
            } catch (SQLException e) {
                warning(e);
                return new ArrayList<>();
            }
        });
    }

    public List<VelocityHistory> getCache() {
        if (database.getType() == VelocityDatabaseType.SQLITE) {
            return fetchHistorySafely(null);
        }

        List<VelocityHistory> cachedList = cache.get(CACHE_ALL_KEY);
        if (cachedList != null) {
            return cachedList;
        }

        List<VelocityHistory> previousList = cache.getPrevious(CACHE_ALL_KEY);
        getHistoryData().thenAccept(historyData -> cache.put(CACHE_ALL_KEY, historyData));

        if (previousList != null) return previousList;
        return fetchHistorySafely(null);
    }

    public List<VelocityHistory> getCache(String caseType) {
        if (database.getType() == VelocityDatabaseType.SQLITE) {
            return fetchHistorySafely(caseType);
        }

        List<VelocityHistory> cachedList = cache.get(caseType);
        if (cachedList != null) {
            return cachedList;
        }

        List<VelocityHistory> previousList = cache.getPrevious(caseType);
        getHistoryData(caseType).thenAccept(historyData -> cache.put(caseType, historyData));

        if (previousList != null) return previousList;
        return fetchHistorySafely(caseType);
    }

    public CompletableFuture<VelocityDatabaseStatus> delAllKeys() {
        return supplyAsync(() -> {
            String sql = "DELETE FROM player_keys";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.executeUpdate();
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public CompletableFuture<VelocityDatabaseStatus> delKeys(String caseType) {
        return supplyAsync(() -> {
            String sql = "DELETE FROM player_keys WHERE case_name = ?";
            try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
                ps.setString(1, caseType);
                ps.executeUpdate();
                return VelocityDatabaseStatus.COMPLETE;
            } catch (SQLException e) {
                warning(e);
                return VelocityDatabaseStatus.FAIL;
            }
        });
    }

    public void close() {
        executor.shutdown();
        cache.clear();
        database.close();
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = database.getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not initialized");
        }
        return connection;
    }

    private String groupColumn() {
        return database.getType() == VelocityDatabaseType.POSTGRESQL ? "\"group\"" : "`group`";
    }

    private String countColumn() {
        return database.getType() == VelocityDatabaseType.POSTGRESQL ? "\"count\"" : "`count`";
    }

    private void insertHistory(VelocityHistory entry) throws SQLException {
        String groupColumn = groupColumn();
        String sql = "INSERT INTO history_data (id, item, player_name, time, " + groupColumn + ", case_type, action) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
            ps.setInt(1, entry.id());
            ps.setString(2, entry.item());
            ps.setString(3, entry.playerName());
            ps.setLong(4, entry.time());
            ps.setString(5, entry.group());
            ps.setString(6, entry.caseType());
            ps.setString(7, entry.action());
            ps.executeUpdate();
        }
    }

    private void updateHistory(long existingTime, String caseType, VelocityHistory data) throws SQLException {
        String groupColumn = groupColumn();
        String sql = "UPDATE history_data SET player_name = ?, time = ?, " + groupColumn + " = ?, action = ? WHERE case_type = ? AND time = ?";
        try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
            ps.setString(1, data.playerName());
            ps.setLong(2, data.time());
            ps.setString(3, data.group());
            ps.setString(4, data.action());
            ps.setString(5, caseType);
            ps.setLong(6, existingTime);
            ps.executeUpdate();
        }
    }

    private List<VelocityHistory> getHistoryDataInternal(String caseType, boolean orderByTime) throws SQLException {
        List<VelocityHistory> result = new ArrayList<>();
        String groupColumn = groupColumn();
        String sql = "SELECT id, item, player_name, time, " + groupColumn + " AS group_name, case_type, action FROM history_data";
        if (caseType != null) {
            sql += " WHERE case_type = ?";
        }
        if (orderByTime) {
            sql += " ORDER BY time ASC";
        }

        try (PreparedStatement ps = requireConnection().prepareStatement(sql)) {
            if (caseType != null) {
                ps.setString(1, caseType);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(VelocityHistory.fromResultSet(rs));
                }
            }
        }
        return result;
    }

    private List<VelocityHistory> fetchHistorySafely(String caseType) {
        if (isDbThread()) {
            try {
                return getHistoryDataInternal(caseType, caseType != null);
            } catch (SQLException e) {
                warning(e);
                return new ArrayList<>();
            }
        }

        if (caseType == null) {
            return getHistoryData().join();
        }
        return getHistoryData(caseType).join();
    }

    private boolean isDbThread() {
        return Thread.currentThread().getName().startsWith(THREAD_NAME_PREFIX);
    }

    private <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    private void warning(Throwable e) {
        logger.warn("Error with database query:", e);
    }

    private static final class NamedThreadFactory implements ThreadFactory {
        private final String baseName;
        private final AtomicInteger index = new AtomicInteger(1);

        private NamedThreadFactory(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName(baseName + "-" + index.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
