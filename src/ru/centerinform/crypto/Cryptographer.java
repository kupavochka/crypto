/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 * @author maria
 */
public class Cryptographer{
    
    private byte[] _plainSignature; //подпись
    
    private void log(String msg)
    {
        System.err.println(msg);
    }
    /**
     * Осуществление подписи данных с помощью смарт-карты
     * @param pass пароль пользователя смарт-карты
     * @param data данные, которые нужно подписать
     * @return подписанные данные
     * @throws PKCS11Exception
     * @throws IOException
     * @throws CertificateException
     * @throws Exception
     */
    public Signature crypt(String pass, String data) 
            throws PKCS11Exception, IOException, CertificateException, Exception{
        JaCarta jc = new JaCarta();
        
        PKCS11 pkcs11Wrapper = jc.InitialazeLib();
        CardInfo[] info = jc.acquireSmartCardList();
        Signature sign = null;
        if (info != null) {
            long session;
            session = jc.SessionInit(info[0].getSlotId(), pass.toCharArray());
            if (session != 0) {
                ArrayList<CardObjectInfo> certsInfo = jc.certificateSearch(session);
                if(certsInfo!= null){
                    Object[] items = certsInfo.toArray();
                    CardObjectInfo first = (CardObjectInfo) items[0];
                    
                    long privateKey = jc.findPrivateKeyById(session, first.getObjectId());

                    try(FileInputStream fin = new FileInputStream(data))
                    {
                        byte[] buffer = new byte[fin.available()];
                        fin.read(buffer, 0, fin.available());
                        sign = sign(session, buffer, privateKey, pkcs11Wrapper, first.getObjectId(), jc);
                    }
                }  
            } else {
                log("Неправильный пароль");
            }
            jc.SessionFinal(session);
        } else {
            log("Вставьте смарт-карту");
        }
        jc.FinalizeLib();
        return sign;
}
    
    private Signature sign(long session, byte[] data, long privateKey, PKCS11 pkcs11Wrapper, byte[] certId, JaCarta jc) 
            throws IOException,SecurityException, NoSuchAlgorithmException, PKCS11Exception, Exception
    {
        byte[] easySignPacket;
        try {
            easySignPacket = createEasySignPacket(session, data, privateKey, pkcs11Wrapper, certId, jc);
        } catch (IOException | SecurityException ex) {
            return null;
        }
        byte[] result = envelopeData(easySignPacket);
        return new Sign(_plainSignature, result);
    }
    
    //Формирование пакета в формате "EASYSIGN"
    private byte[] createEasySignPacket(long session, byte[] data, long privateKey, PKCS11 pkcs11Wrapper, byte[] certId, JaCarta jc) 
            throws IOException,SecurityException, PKCS11Exception, Exception
    {
        try(ByteArrayInputStream inStm = new ByteArrayInputStream(data))
        {
            try(ByteArrayOutputStream outStm = new ByteArrayOutputStream())
            {
                StreamSigner streamSigner = new StreamSigner(inStm, outStm);
                JaCartaSigner jcSigner = new JaCartaSigner(session, privateKey, pkcs11Wrapper);
                streamSigner.sign(jcSigner, certId, jc);
                
                _plainSignature = streamSigner.getPlainSignature();
                
                return outStm.toByteArray();
            }
        }    
    }
    
    //Формирование пакета в формате "CRYPTENV"
    private byte[] envelopeData(byte[] data) throws IOException, NoSuchAlgorithmException
    {
         try(ByteArrayInputStream inStm = new ByteArrayInputStream(data))
        {
            try(ByteArrayOutputStream outStm = new ByteArrayOutputStream())
            {
                StreamEnveloper streamEnveloper = new StreamEnveloper((inStm), outStm);
                Hash hash = new Hash();
                streamEnveloper.pack(hash);
                
                return outStm.toByteArray();
            }
        }    
    }
    
}
