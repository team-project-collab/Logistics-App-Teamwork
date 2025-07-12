package com.company.oop.logistics.db;

public interface PersistenceManager {
    <T> void saveData(T data, String filePath);
    <T> T loadData(String filePath);

}
