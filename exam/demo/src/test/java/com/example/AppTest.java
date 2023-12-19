package com.example;

import org.junit.jupiter.api.Test;

import com.example.DataManager.DataManager;
import com.example.DataManager.PublicationFactory;
import com.example.Entity.Publication;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
class AppTest {
    DataManager dm = new DataManager(PublicationFactory.initSampleData(), "S");
    @Test
    void testGet() {
        assertEquals(dm.getPublication("S-1").getID(), "S-1");
    }

    @Test
    void testGetAll(){
        assertEquals(dm.getPublications().size(), 9);
    }

    @Test
    void testAdd(){
        dm.addPublication(new Publication("S-10", "n", "n", "2004-04-04", 200));
        assertEquals(dm.getPublications().size(), 10);
        assertEquals(dm.getPublication("S-10").getID(), "S-10");
    }

    @Test
    void testUpdate(){
        dm.updatePublication(new Publication("S-1", "n", "n", "2004-04-04", 200));
        assertEquals(dm.getPublication("S-1").getAuthor(), "n");
    }

    @Test
    void testDelete(){
        dm.deletePublication("S-10");
        assertEquals(dm.getPublications().size(), 9);
        assertEquals(dm.getPublication("S-10"), null);
    }

}
