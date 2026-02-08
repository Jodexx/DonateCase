package com.jodexindustries.velocity.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.helpers.NOPLogger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VelocityCaseDatabaseTest {

    @TempDir
    Path tempDir;

    private VelocityDatabase database;
    private VelocityCaseDatabase caseDatabase;

    @BeforeEach
    void setUp() {
        database = new VelocityDatabase(NOPLogger.NOP_LOGGER);
        database.connect(tempDir, VelocityDatabaseType.SQLITE, null);
        caseDatabase = new VelocityCaseDatabase(database, NOPLogger.NOP_LOGGER, 20);
    }

    @AfterEach
    void tearDown() {
        if (caseDatabase != null) {
            caseDatabase.close();
        } else if (database != null) {
            database.close();
        }
    }

    @Test
    void setAndGetKeys() {
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setKeys("caseA", "player1", 5).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setKeys("caseB", "player1", 2).join());

        assertEquals(5, caseDatabase.getKeys("caseA", "player1").join());
        assertEquals(2, caseDatabase.getKeys("caseB", "player1").join());

        Map<String, Integer> allKeys = caseDatabase.getKeys("player1").join();
        assertEquals(2, allKeys.size());
        assertEquals(5, allKeys.get("caseA"));
        assertEquals(2, allKeys.get("caseB"));

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setKeys("caseA", "player1", 9).join());
        assertEquals(9, caseDatabase.getKeys("caseA", "player1").join());
    }

    @Test
    void setKeysBulkAndDelete() {
        Map<String, Integer> bulk = new HashMap<>();
        bulk.put("player1", 1);
        bulk.put("player2", 3);

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setKeysBulk("caseBulk", bulk).join());

        assertEquals(1, caseDatabase.getKeys("caseBulk", "player1").join());
        assertEquals(3, caseDatabase.getKeys("caseBulk", "player2").join());

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.delKeys("caseBulk").join());
        assertEquals(0, caseDatabase.getKeys("caseBulk", "player1").join());

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setKeys("caseBulk", "player1", 7).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.delAllKeys().join());
        assertEquals(0, caseDatabase.getKeys("caseBulk", "player1").join());
    }

    @Test
    void openCountOperations() {
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setCount("caseA", "player1", 2).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setCount("caseB", "player1", 5).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setCount("caseA", "player2", 3).join());

        assertEquals(2, caseDatabase.getOpenCount("player1", "caseA").join());
        assertEquals(5, caseDatabase.getOpenCount("player1", "caseB").join());

        Map<String, Integer> player1Counts = caseDatabase.getOpenCount("player1").join();
        assertEquals(2, player1Counts.size());
        assertEquals(2, player1Counts.get("caseA"));
        assertEquals(5, player1Counts.get("caseB"));

        Map<String, Map<String, Integer>> global = caseDatabase.getGlobalOpenCount().join();
        assertEquals(2, global.size());
        assertEquals(2, global.get("player1").get("caseA"));
        assertEquals(5, global.get("player1").get("caseB"));
        assertEquals(3, global.get("player2").get("caseA"));

        Map<String, Integer> globalCaseA = caseDatabase.getGlobalOpenCount("caseA").join();
        assertEquals(2, globalCaseA.size());
        assertEquals(2, globalCaseA.get("player1"));
        assertEquals(3, globalCaseA.get("player2"));
    }

    @Test
    void historyAddAndLimit() {
        String caseType = "caseHistory";
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.addHistory(caseType, history(1, caseType, 1L), 2).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.addHistory(caseType, history(2, caseType, 2L), 2).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.addHistory(caseType, history(3, caseType, 3L), 2).join());

        List<VelocityHistory> history = caseDatabase.getHistoryData(caseType).join();
        assertEquals(2, history.size());
        assertEquals(2L, history.get(0).time());
        assertEquals(3L, history.get(1).time());
    }

    @Test
    void historySetAndRemove() {
        String caseType = "caseUpdate";

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.addHistory(caseType, history(1, caseType, 10L), 10).join());
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.addHistory(caseType, history(2, caseType, 20L), 10).join());

        VelocityHistory updated = new VelocityHistory(1, "item-1", "player-x", 30L, "group-x", caseType, "action-x");
        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.setHistoryData(caseType, 0, updated).join());

        List<VelocityHistory> history = caseDatabase.getHistoryData(caseType).join();
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(entry -> entry.time() == 30L && "player-x".equals(entry.playerName())));

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.removeHistoryData(caseType, 1).join());
        List<VelocityHistory> afterRemoveById = caseDatabase.getHistoryData(caseType).join();
        assertFalse(afterRemoveById.stream().anyMatch(entry -> entry.id() == 1));

        assertEquals(VelocityDatabaseStatus.COMPLETE, caseDatabase.removeHistoryData(caseType).join());
        List<VelocityHistory> afterClear = caseDatabase.getHistoryData(caseType).join();
        assertNotNull(afterClear);
        assertTrue(afterClear.isEmpty());
    }

    private static VelocityHistory history(int id, String caseType, long time) {
        return new VelocityHistory(id, "item-" + id, "player-" + id, time, "group-" + id, caseType, "action-" + id);
    }
}
