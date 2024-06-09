import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.saha.KeyValueStore;

import static org.junit.jupiter.api.Assertions.*;

public class KeyValueStoreTest {
    private KeyValueStore<String,String> kvStore;

    @BeforeEach
    void setUp(){
        kvStore = new KeyValueStore<>();
    }

    @Test
    void testBasicOperations(){
        kvStore.update("key1","value1");
        assertEquals("value1",kvStore.get("key1"));
        kvStore.delete("key1");
        assertNull(kvStore.get("key1"));
    }

    @Test
    void testTransactionCommit()  {
        kvStore.update("key1","value1");
        kvStore.beginTransaction();
        kvStore.update("key1","value2");
        kvStore.update("key2","value2");

        assertEquals("value2",kvStore.get("key1"));
        assertEquals("value2",kvStore.get("key2"));

        kvStore.commitTransaction();

        assertEquals("value2",kvStore.get("key1"));
        assertEquals("value2",kvStore.get("key2"));
    }

    @Test
    void testTransactionRollback(){

    }

}
