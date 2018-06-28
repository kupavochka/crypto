/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Упаковка подписанных блоков данных в формат "CRYPTENV" со смещенным хешом от константы и тела
 * @author maria
 */
public class StreamEnveloper {
    private final InputStream _inStm;
    private final OutputStream _outStm;
    private final int BUFF_SIZE = 8192;
    private final String kEnvelopePrefix = "CRYPTENV";
    private final byte kStreamVersion = 1;
    private final String kSecret = "Asd1%K,sde"; 
    
    /**
     * 
     * @param inStm абстрактный входной поток с байтами
     * @param outStm абстрактный выходной поток
     */
    public StreamEnveloper(InputStream inStm, OutputStream outStm)  
    {
        if(null == inStm)
            throw new IllegalArgumentException("Input stream is not set");
        if(null == outStm)
            throw new IllegalArgumentException("Output stream is not set");
        
        _inStm = inStm;
        _outStm = outStm;
    }
    
    private void writePrefix() throws IOException
    {
        byte[] bytes = kEnvelopePrefix.getBytes();
        _outStm.write(bytes);
    }
    
    
    private void writeStreamVersion() throws IOException
    {
        DataOutputStream dos = new DataOutputStream(_outStm);
        dos.writeByte(kStreamVersion);
    }
    
    
    public void pack(Hash hash) throws IOException
    {
        writePrefix();
        writeStreamVersion();
        
        hash.update(kSecret);
        
        byte[] buffer = new byte[BUFF_SIZE];
        while(true)
        {
            int rv =  _inStm.read(buffer, 0, BUFF_SIZE); // считать может меньше!!!!
            if(rv == -1) break;
            
            hash.update(buffer, 0, rv);
            _outStm.write(buffer, 0, rv);
        }
        
       _outStm.write(hash.getHashValue());
    }
    
}
