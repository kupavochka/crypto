/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

/**
 * Представление байт в обратном порядке
 * @author maria
 */
public class Utils {
    public static byte[] reverse(byte[] bytes) throws IllegalArgumentException 
    {
        if(bytes.length == 0)    
            throw new IllegalArgumentException("Bytes count == 0");
        
        byte[] result = new byte[bytes.length]; 

        //меняет местами байты в массиве, то есть result[0] = bytes[63]
        for(int i = 0; i < bytes.length; i++)
	{
            int ix = bytes.length - i - 1;
            result[ix] = bytes[i];
	}
        
        return result;
    } 
    
}
