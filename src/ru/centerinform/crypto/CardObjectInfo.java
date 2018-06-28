/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

/**
 * Инофрмация об объекте на смарт-карте
 * @author maria
 */
public class CardObjectInfo {
    private final byte[] _bytes;
    private final String _subject;
 
    public CardObjectInfo(byte[] bytes, String subject)
    {
        _bytes = bytes;
        _subject = subject;
    }
        
    
    public byte[] getObjectId()
    {
        return _bytes;
    }
    
    @Override
    public String toString()
    {
        return new String(_bytes) + ":" + _subject;
    }
}
