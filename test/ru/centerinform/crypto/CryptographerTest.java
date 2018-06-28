/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.FileOutputStream;
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
public class CryptographerTest {
    
    public CryptographerTest() {
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
     * Test of crypt method, of class Cryptographer.
     */
    private final String TEST_DATA = "c:\\Users\\maria\\Documents\\utils\\testdata.txt";
    private final String FILE_NAME = "c:\\Users\\maria\\Documents\\utils\\test.signed.env";
    
    @Test
    public void testCrypt() throws Exception {
        System.out.println("crypt test");
        String password = "123456";
        
        Cryptographer instance = new Cryptographer();
        Signature result = instance.crypt(password, TEST_DATA);
        assertTrue("Error cryptographer", result != null);
        saveToFile(FILE_NAME, result.getEasySign());
    }
    private void saveToFile(String filePath, byte[] data) throws Exception
    {
        System.out.println("Save to file...");
        try(FileOutputStream outStm = new FileOutputStream(filePath))
        {
            outStm.write(data, 0, data.length);
        }
    }
}
