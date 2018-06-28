/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
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
public class HashTest {
    
    public HashTest() {
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
     * Test of update method, of class Hash.
     */
    private final String arg1 = "Эти ягоды, все знают,нам лекарство заменяют.";
    private final String arg2 = "Если вы больны ангиной,пейте на ночь чай с...";
    
    private void updateHash(Hash instance, String arg) throws UnsupportedEncodingException
    {
        byte[] bytes = arg.getBytes("UTF-8");
        instance.update(bytes);
    }
    
    private static String toHex(byte[] bytes) 
    {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
    
    @Test
    public void testUpdate_byteArr() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("update byte array");
        Hash instance = new Hash();
        
        updateHash(instance, arg1);
        updateHash(instance, arg2);
        
        byte[] digest = instance.getHashValue();
        System.out.print(toHex(digest));
        String expectedValue = "ba3325467e50fb6f437a8122f46cabac4dc84ee2";
        assertEquals("Неожиданный хеш",  expectedValue.toUpperCase(), toHex(digest));
    }

    /**
     * Test of update method, of class Hash.
     */

    /**
     * Test of update method, of class Hash.
     */
    @Test
    public void testUpdate_String() throws NoSuchAlgorithmException {
        System.out.println("update string");
        String str = "What is love?";
        Hash instance = new Hash();
        instance.update(str);
        
        byte[] value = instance.getHashValue();
        String hexval = toHex(value);
      
        String expectedHashValue = "6f9150876e1c3932a2d01afd7d77fec24d64c5e2";
        assertEquals("Неожиданный результат хеширования", expectedHashValue.toUpperCase() ,  hexval);
    }

    /**
     * Test of getHashValue method, of class Hash.
     */
    
}
