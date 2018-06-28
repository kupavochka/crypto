/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Рассчет хеша SHA-1
 * @author maria
 */
public class Hash {
    private final MessageDigest _digest;
    
    public Hash() throws NoSuchAlgorithmException
    {
            _digest = MessageDigest.getInstance("SHA1");
    }
    
    public void update(byte[] bytes) throws IllegalArgumentException
    {
        if(bytes == null)
            throw new IllegalArgumentException("No bytes");
        
        _digest.update(bytes);
    }
    
    public void update(byte[] bytes,int offset, int len) throws IllegalArgumentException
    {
        if(bytes == null)
            throw new IllegalArgumentException("No bytes");
        
        _digest.update(bytes, offset, len);
    }
    
    
    public void update(String str) throws IllegalArgumentException
    {
        if(str == null)
            throw new IllegalArgumentException("String is null");
        if(str.isEmpty())
            throw new IllegalArgumentException("String is empty");
                    
        byte[] bytes = str.getBytes();
        _digest.update(bytes);
    }
    
    
    public byte[] getHashValue()
    {
        return _digest.digest();
    }
}
