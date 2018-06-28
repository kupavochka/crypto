/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import static sun.security.pkcs11.wrapper.PKCS11Constants.NULL_PTR;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 *
 * @author maria
 */
public class JaCarta {

    /**
     * @param args the command line arguments
     */
    private PKCS11 pkcs11Wrapper;

    private void log(String msg)
    {
        System.err.println(msg);
    }
    
    /**
     * Инициализация библиотеки
     * @return инициазированная библиотека
     * @throws PKCS11Exception
     * @throws IOException
     */
    public PKCS11 InitialazeLib() throws PKCS11Exception, IOException {
        log("Initalaze library...");
        CK_C_INITIALIZE_ARGS ck_c_initialize_args = new CK_C_INITIALIZE_ARGS();
        pkcs11Wrapper = PKCS11.getInstance("jcpkcs11-2.dll", "C_GetFunctionList", ck_c_initialize_args, false);
        return pkcs11Wrapper;
    }

    /**
     *
     * @return информация о вставленной смарт-карте(есть)
     * @throws PKCS11Exception не найдена информация о токене
     */
    public CardInfo[] acquireSmartCardList() throws PKCS11Exception {

        long[] slotList = pkcs11Wrapper.C_GetSlotList(true);
        int slotCount = slotList.length;
        if (slotCount == 0) {
            return null;
        }

        CardInfo[] result = new CardInfo[slotCount];
        for (int i = 0; i < slotCount; i++) {
            CK_TOKEN_INFO info = pkcs11Wrapper.C_GetTokenInfo(slotList[i]);
            result[i] = new CardInfo(new String(info.serialNumber), slotList[i]);
        }

        return result;
    }

    /**
     * Открытие сессии с пин-кодом
     * @param slotId номер слота, куда вставлена смарт карта
     * @param pass пароль пользователя смарт-карты
     * @return открытие сессии
     * @throws PKCS11Exception ошибка входа
     * @throws IOException
     * @throws CertificateException
     * @throws Exception
     */
    public long SessionInit(long slotId, char[] pass) throws PKCS11Exception, IOException, CertificateException, Exception {
        long session;
        session = pkcs11Wrapper.C_OpenSession(slotId, PKCS11Constants.CKF_SERIAL_SESSION | PKCS11Constants.CKF_RW_SESSION, null, null);
        log("Open session...");
        try {
            pkcs11Wrapper.C_Login(session, PKCS11Constants.CKU_USER, pass);
        } catch (PKCS11Exception e) {
            log(e.getMessage());
            return 0;
        }

        log("Login");
        return session;

    }

    /**
     * Закрытие сессии
     * @param session открытая сессия
     * @throws PKCS11Exception если сессия была не открыта
     */
    public void SessionFinal(long session) throws PKCS11Exception {
        try {
            pkcs11Wrapper.C_CloseSession(session);
            log("Close session");
        } catch (PKCS11Exception e) {
            log(e.getMessage());
        }

    }

    /**
     * Поиск сертификата
     * @param session открытая сессия
     * @return информация о сертификате
     * @throws PKCS11Exception
     * @throws IOException
     * @throws CertificateException
     */
    public ArrayList<CardObjectInfo> certificateSearch(long session) throws PKCS11Exception, IOException, CertificateException {
        // задаем шаблон поиска сертификата
        CK_ATTRIBUTE attrs[] = {new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, true),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_CERTIFICATE),};
        pkcs11Wrapper.C_FindObjectsInit(session, attrs); //Инициализирует поиск объектов, которые совпадают с заданным шаблоном.

        long[] foundObjs = pkcs11Wrapper.C_FindObjects(session, 100);//осуществляет поиск объектов, которые совпадают с заданным шаблоном.

        if (foundObjs.length == 0) {
            log("End of search");
            return null;
        }
        ArrayList<CardObjectInfo> infocerf = new ArrayList<>();
        for (int i = 0; i < foundObjs.length; i++) {
            long objectHandle = foundObjs[i];

            byte[] objectId = getObjectId(session, objectHandle);
            byte[] certBody = getObjectBody(session, objectHandle);

            infocerf.add(new CardObjectInfo(objectId, getCertificateSubject(certBody)));

        }
        pkcs11Wrapper.C_FindObjectsFinal(session);
        return infocerf;
    }

    private byte[] getObjectId(long session, long objectHandle) throws PKCS11Exception {
        CK_ATTRIBUTE[] reqAttrs = {new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, 0)};
        pkcs11Wrapper.C_GetAttributeValue(session, objectHandle, reqAttrs); //Получает значение одного или нескольких аттрибутов для указанного объекта.

        byte[] id = (byte[]) reqAttrs[0].pValue;
        return id;
    }

    private byte[] getObjectBody(long session, long objectHandle) throws PKCS11Exception {
        CK_ATTRIBUTE[] reqAttrs = {new CK_ATTRIBUTE(PKCS11Constants.CKA_VALUE, 0)};
        pkcs11Wrapper.C_GetAttributeValue(session, objectHandle, reqAttrs);

        byte[] id = (byte[]) reqAttrs[0].pValue;
        return id;
    }

    private String getCertificateSubject(byte[] encodedCert) throws CertificateException, IOException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        try (InputStream in = new ByteArrayInputStream(encodedCert)) {
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);

            return cert.getSubjectDN().getName(); // имя атрибута
        }
    }

    /**
     * Поиск закрытого ключа
     * @param session открытая сессия
     * @param privKeyId id найденного закрытого ключа (атрибута)
     * @return закрытый ключ
     * @throws PKCS11Exception
     * @throws Exception
     */
    public long findPrivateKeyById(long session, byte[] privKeyId) throws PKCS11Exception, Exception {
        log("findPrivateKeyById");

        CK_ATTRIBUTE[] attrs = {
            new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_PRIVATE_KEY),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, true),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_PRIVATE, true),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, privKeyId)
        };

        pkcs11Wrapper.C_FindObjectsInit(session, attrs);
        try {
            long[] objects = pkcs11Wrapper.C_FindObjects(session, 1);
            if (objects.length == 0) {
                throw new Exception("Error - the private key wasnt found. The card has an inconsistent data");
            }

            return objects[0];
        } finally {
            pkcs11Wrapper.C_FindObjectsFinal(session);
        }
    }

    /** 
     * Сам сертификат
     * @param session открытая сессия
     * @param certId id найденного сертификата(атрибута)
     * @return тело сертификата
     * @throws PKCS11Exception
     * @throws Exception
     */
    public byte[] getCertificateBody(long session, byte[] certId) throws PKCS11Exception, Exception {
        log("getCertificateBody");
        //задаем шаблон
        CK_ATTRIBUTE[] attrs = {
            new CK_ATTRIBUTE(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_CERTIFICATE),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_TOKEN, true),
            new CK_ATTRIBUTE(PKCS11Constants.CKA_ID, certId)
        };

        pkcs11Wrapper.C_FindObjectsInit(session, attrs);
        try {
            long[] objects = pkcs11Wrapper.C_FindObjects(session, 1);
            if (objects.length == 0) {
                throw new Exception("Error - the certificate key wasnt found. The card has an inconsistent data");
            }

            long certHandle = objects[0];
            return getObjectBody(session, certHandle);
        } finally {
            pkcs11Wrapper.C_FindObjectsFinal(session);
        }
    }

    /**
     * Финализация библиотеки
     * @throws PKCS11Exception
     * @throws IOException
     */
    public void FinalizeLib() throws PKCS11Exception, IOException {
        log("Final");
        pkcs11Wrapper.C_Finalize(NULL_PTR);
    }
}
