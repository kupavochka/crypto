/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.*;

/**
 * Упаковка единичного блока TLV формата "EASYSIGN"
 * @author maria
 */
public class TLVblock {
    
    public enum BlockType {DATA, SIGNATURE, CERTIFICATE};
    public static final int MAX_BLOCK_SIZE = 16381; // 1 блок не больше 16 КБ
    
    private final byte[] _data;
    private final BlockType _type;
    private final int _actLen;
    
    /**
     * 
     * @param type тип блока
     * @param data    данные блока
     * @param actualLength  длина блока
     */
    public TLVblock(BlockType type, byte[] data, int actualLength) throws IllegalArgumentException
    {
        if(data == null)
            throw new IllegalArgumentException("data is not set");
        if(data.length > MAX_BLOCK_SIZE)
            throw new IllegalArgumentException("data is too big");
        
        _data = data;
        _type = type;
        _actLen = actualLength;
    }
    
    private void writeTagField(OutputStream out) throws IOException
    {
        int temp = _type.ordinal();
        out.write(temp); // Only one byte is written, java-specific
    }
    
    private void writeLengthField(OutputStream out) throws IOException
    {
         short x = (short)_actLen;
        //
        //  Немного мудрим - пишем длину побайтно, little endian
        //
        byte[] ret = new byte[2];
        ret[0] = (byte) x;
        ret[1] = (byte) (x >> 8);
        
        out.write(ret);
    }
    
    public void write(OutputStream out) throws IOException, IllegalArgumentException
    {
        if(out == null)
            throw new IllegalArgumentException("stream is not set");
        
        writeTagField(out);  
        writeLengthField(out);
        //
        //  So, write data itself
        //
        out.write(_data, 0, _actLen);
    }
}
