package testingUtils;

import com.company.oop.logistics.db.PersistenceManager;

import java.util.ArrayList;

public class MockPersistenceManagerImpl implements PersistenceManager {
    public <T> void saveData(T data, String filePath) {
    }

    public <T> T loadData(String filePath){
        return (T) new ArrayList<>();
    }
}
