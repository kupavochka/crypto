/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 * Интерфейс(конструктор) для осуществления подписи данных с помощью смарт-карты
 * @author maria
 */
public interface SCardSigner {
    public void   initSignature() throws PKCS11Exception;
    public void   updateSignature(byte[] data, int len) throws PKCS11Exception;
    public void   updateSignature(byte[] data) throws PKCS11Exception;
    public byte[] getSignatureValue() throws SecurityException, IOException, IllegalStateException, 
            CertificateException, Exception;
    public byte[] getSignerCertificateAsByteArray(byte[] certId, JaCarta jc) throws SecurityException, IOException, IllegalStateException, 
            CertificateException, Exception;
}
