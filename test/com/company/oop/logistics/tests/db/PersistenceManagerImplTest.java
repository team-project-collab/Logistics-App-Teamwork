package com.company.oop.logistics.tests.db;

import com.company.oop.logistics.db.PersistenceManager;
import com.company.oop.logistics.db.PersistenceManagerImpl;
import com.company.oop.logistics.models.LocationImpl;
import com.company.oop.logistics.models.contracts.Location;
import com.company.oop.logistics.models.enums.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManagerImplTest {
    private static final String TEST_PATH = "test-data/locations.xml";
    private static final String FAKE_PATH = "fakePath/locations.xml";
    private List<Location> locationList;
    private PersistenceManager persistenceManager;
    private LocalDateTime now;
    private Location location1;

    @BeforeEach
    public void setUp(){
        now = LocalDateTime.now();
        locationList = new ArrayList<>();
        createTestFolderIfNotExist();
        cleanTestFile();
        persistenceManager = new PersistenceManagerImpl();
        location1 = new LocationImpl(5, City.SYD, now.plusMinutes(1), now.plusMinutes(2));
        locationList.add(location1);
    }

    @Test
    public void loadData_Should_LoadList(){
        persistenceManager.saveData(locationList, TEST_PATH);
        locationList = persistenceManager.loadData(TEST_PATH);
        Assertions.assertEquals(
                location1.getDepartureTime(),
                locationList.get(locationList.size() - 1).getDepartureTime()
        );
    }

    @Test
    public void saveData_Should_Throw_When_InvalidPath(){
        Assertions.assertThrows(
                RuntimeException.class,
                () -> persistenceManager.saveData(locationList, FAKE_PATH)
        );
    }

    @Test
    public void loadData_Should_ReturnEmpty_When_InvalidPath(){
        locationList = persistenceManager.loadData(FAKE_PATH);
        Assertions.assertEquals(
                0,
                locationList.size()
        );
    }

    private void cleanTestFile() {
        File file = new File(TEST_PATH);
        if (file.exists()) file.delete();
    }

    private void createTestFolderIfNotExist(){
        File folder = new File(TEST_PATH.split("/")[0]);
        if (!folder.exists()){
            boolean created = folder.mkdir();
        }
    }
}
