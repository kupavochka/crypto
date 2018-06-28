/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author maria
 */
public class StreamEnveloperTest {
    
    public StreamEnveloperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of pack method, of class StreamEnveloper.
     */
    private final String TEXT = "Тут какие-то ну очень большие данные, да еще и текстовые";
    @Test
    public void testPack() throws Exception {
        System.out.println("pack");
        try(ByteArrayInputStream instm = new ByteArrayInputStream(TEXT.getBytes("UTF-8")))
        {
            try(ByteArrayOutputStream outstm = new ByteArrayOutputStream(1000))
            {
                Hash hash = new Hash();
                StreamEnveloper instance = new StreamEnveloper(instm, outstm);
                instance.pack(hash);
                
                assertTrue("Выходной поток не может быть пуст", outstm.size() != 0);
            }
        }
    }
}
