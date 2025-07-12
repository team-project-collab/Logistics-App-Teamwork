package com.company.oop.logistics.db;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.ArrayList;

public class PersistenceManagerImpl implements PersistenceManager {
    private final XStream xStream;

    public PersistenceManagerImpl() {
        xStream = new XStream();
        xStream.allowTypesByWildcard(new String[] {
                "com.company.oop.logistics.models.**"
        });
    }

    public <T> void saveData(T data, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream out = xStream.createObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to file: " + filePath, e);
        }
    }

    public <T> T loadData(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return (T) new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream in = xStream.createObjectInputStream(fis)) {
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load data from file: " + filePath, e);
        }
    }
}