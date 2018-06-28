/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 *
 * @author maria
 */
public class JaCartaSigner implements SCardSigner {

    private final long _session;
    private final long _privKeyAlias;
    private final PKCS11 _pkcs11Wrapper;

    private void log(String msg)
    {
        System.err.println(msg);
    }
    
    /**
     *
     * @param session открытая сессия
     * @param privKeyAlias закрытый ключ
     * @param pkcs11Wrapper инициазированная библиотека
     */
    public JaCartaSigner(long session, long privKeyAlias, PKCS11 pkcs11Wrapper) {
        _session = session;
        _privKeyAlias = privKeyAlias;
        _pkcs11Wrapper = pkcs11Wrapper;
    }

 
    private final static int CKM_GOSTR3410_WITH_GOSTR3411 = 0x00001202;
    //механизм для генерации и проверки ЭП (ГОСТ Р 34.10-2001) с хэшированием (ГОСТ Р 34.11-94) подаваемых на вход данных. Размер данных не ограничен.

    /**
     * Инициализация сигнатуры(подписи)
     * @throws PKCS11Exception
     */
    @Override
    public void initSignature() throws PKCS11Exception {
        //Инициализирует операцию подписи для указанного ключа, выработки имитовставки и вычисления HMAC.
        _pkcs11Wrapper.C_SignInit(_session, new CK_MECHANISM(CKM_GOSTR3410_WITH_GOSTR3411), _privKeyAlias);
       
    }

    /**
     * Подпись данных
     * @param data данные, которые нужно подписать
     * @param len длина данных
     * @throws PKCS11Exception
     */
    @Override
    public void updateSignature(byte[] data, int len) throws PKCS11Exception {
        log("updateSignature");
        if (data == null) {
            throw new IllegalArgumentException("Incorrect input data");
        }
        _pkcs11Wrapper.C_SignUpdate(_session, 0, data, 0, len);//Продолжает составную операцию подписи – 
        //подписывает очередной блок данных, вычисляет HMAC или вырабатывает имитовставку для очередного блока данных.
    }

    /**
     * Подпись данных
     * @param data данные, которые нужно подписать
     * @throws PKCS11Exception
     */
    @Override
    public void updateSignature(byte[] data) throws PKCS11Exception {
        log("updateSignature");
        if (data == null) {
            throw new IllegalArgumentException("Incorrect input data");
        }
        _pkcs11Wrapper.C_SignUpdate(_session, 0, data, 0, data.length);//Продолжает составную операцию подписи – 
        //подписывает очередной блок данных, вычисляет HMAC или вырабатывает имитовставку для очередного блока данных.
    }
    
    /**
     * Сама подпись
     * @return подпись
     * @throws SecurityException
     * @throws IOException
     * @throws IllegalStateException
     * @throws CertificateException
     * @throws Exception
     */
    @Override
    public byte[] getSignatureValue() throws SecurityException, IOException, IllegalStateException, CertificateException, Exception {
        return Utils.reverse(_pkcs11Wrapper.C_SignFinal(_session, 64)); // подпись
        
    }
    
    /**
     * Сертификат
     * @param certId id найденного сертификата(атрибута)
     * @param jc объект класса JaCarta
     * @return тело сертификата
     * @throws SecurityException
     * @throws IOException
     * @throws IllegalStateException
     * @throws CertificateException
     * @throws Exception
     */
    @Override
    public byte[] getSignerCertificateAsByteArray(byte[] certId, JaCarta jc) throws SecurityException, IOException, IllegalStateException, CertificateException, Exception {
        return jc.getCertificateBody(_session, certId); //сертификат
        
    }
}
