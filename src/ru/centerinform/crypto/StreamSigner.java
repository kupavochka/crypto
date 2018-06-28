/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 *
 * @author maria
 */
public class StreamSigner {
    private final InputStream _inStm;
    private final OutputStream _outStm;
    private final String STREAM_PREFIX = "EASYSIGN";
    private byte[] _plainSignature;
        
    /**
     *
     * @return подпись
     */
    public byte[] getPlainSignature()
    {
        return _plainSignature;
    }
    
    /**
     * Конструктор для потоков
     * @param in Входной поток данных
     * @param out Выходной поток данных
     */
    public StreamSigner(InputStream in, OutputStream out) throws IllegalArgumentException
    {
        if(in == null)
            throw new IllegalArgumentException("No input stream is supplied");
        if(out == null)
            throw new IllegalArgumentException("No output stream is supplied");
        
        _inStm = in;
        _outStm = out;
        _plainSignature = null;
    }

    private void writePrefix() throws IOException
    {
        byte[] bytes = STREAM_PREFIX.getBytes();
        _outStm.write(bytes);
    }
    
    private void log(String msg)
    {
        System.err.println(msg);
    }
    
    /**
     * Осуществление подписи TLV блоков данных
     * @param signer экземпляр интерфейса SCardSigner
     * @param certId id найденного сертификата(атрибута)
     * @param jc объект класса JaCarta
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @throws PKCS11Exception
     * @throws Exception
     */
    public void sign(SCardSigner signer, byte[] certId, JaCarta jc ) throws IllegalArgumentException, IOException, SecurityException, PKCS11Exception, Exception
    {
        if(signer == null)
            throw new IllegalArgumentException("No signer object");
        
        signer.initSignature(); //инициализация подписи
        
        writePrefix();
        
        byte[] buffer = new byte[TLVblock.MAX_BLOCK_SIZE]; 
        
        while(true)
        {
            int actrd = _inStm.read(buffer, 0, TLVblock.MAX_BLOCK_SIZE); // 1 блок не больше 16 КБ
            if(actrd == -1) 
                break;
        
            signer.updateSignature(buffer, actrd);
            
            TLVblock tlv = new TLVblock(TLVblock.BlockType.DATA, buffer, actrd);
            
            tlv.write(_outStm);
            log("TLV update");
            
        }
        
        writeSignatureBlock(signer);
        writeCertificateBlock(signer, certId, jc);        
    }

    
    private void writeCertificateBlock(SCardSigner signer, byte[] certId, JaCarta jc) throws IOException, SecurityException, Exception
    {
        byte[] serSignerCert = signer.getSignerCertificateAsByteArray(certId, jc);
        TLVblock certBlock = new TLVblock(TLVblock.BlockType.CERTIFICATE, serSignerCert, serSignerCert.length);
        certBlock.write(_outStm);
    }

    
    private void writeSignatureBlock(SCardSigner signer) throws IOException, SecurityException, Exception
    {
        byte[] signature = signer.getSignatureValue();
        _plainSignature = signature;
        TLVblock signatureBlock = new TLVblock(TLVblock.BlockType.SIGNATURE, signature, signature.length);
        signatureBlock.write(_outStm);
    }
}
