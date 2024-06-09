package org.saha;

import java.util.concurrent.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KeyValueStore<K extends Serializable,V extends Serializable> {
    private final Map<K,V> store;

    private Map<K,V> transactionStore;

    private boolean inTransaction;

    public KeyValueStore(){
        this.store = new ConcurrentHashMap<>();
        this.transactionStore = null;
        this.inTransaction = false;
    }

    //Begin the transaction
    public void beginTransaction() {
        if(inTransaction){
            throw new IllegalStateException("Transaction already in progress");
        }
        transactionStore = new HashMap<>(store);
        inTransaction = true;
    }

    //commit the transaction
    public void rollbackTransaction(){
        if(!inTransaction){
            throw new IllegalStateException("No transaction in progress");
        }
        inTransaction = false;
        transactionStore = store;
    }

    //Get method
    public V get(K key){
        if(inTransaction){
            return transactionStore.get(key);
        }
        return store.get(key);
    }

    //update method
    public void update(K key,V value){
        if(inTransaction){
            transactionStore.put(key,value);
        }else {
            store.put(key, value);
        }
    }

    //Delete method
    public boolean delete(K key){
        if(inTransaction){
            return transactionStore.remove(key) != null;
        }
        return store.remove(key) != null;
    }

    public void commitTransaction(){
        if(!inTransaction){
            throw new IllegalStateException("transaction is not active");
        }
        store.putAll(transactionStore);
    }

    @Override
    public String toString(){
        return inTransaction ? transactionStore.toString() : store.toString();
    }

    public void SaveToFile(String filename) throws IOException {
        try(FileOutputStream fileOut  = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(store);
        }
    }
    public void loadFromFile(String filename) throws IOException,ClassNotFoundException{
        try(FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn)){
            store.clear();
            store.putAll((Map<K,V>) in.readObject());
        }
    }

}
