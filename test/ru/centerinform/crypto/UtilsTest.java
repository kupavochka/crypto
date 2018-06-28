/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

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
public class UtilsTest {
    
    public UtilsTest() {
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
     * Test of reverse method, of class Utils.
     */
    @Test
    public void testReverse() {
        System.out.println("Reverse bytes");
        byte[] bytes = {1, 2, 3, 4, 5, 6};
        byte[] expResult = {6, 5, 4, 3, 2, 1};
        byte[] result = Utils.reverse(bytes);
        assertArrayEquals(expResult, result);
    }
    
}
