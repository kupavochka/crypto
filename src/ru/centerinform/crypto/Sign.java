/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.centerinform.crypto;

/**
 *
 * @author maria
 */
public class Sign implements Signature
{
    private final byte[] _plainSign;
    private final byte[] _easySign;
    
    /**
     *
     * @param plainSign обычная подпись
     * @param easySign подпись в формате EASYSIGN
     */
    public Sign(byte[] plainSign, byte[] easySign)
    {
        _plainSign = plainSign;
        _easySign = easySign;
    }
    
    /**
     *
     * @return
     */
    @Override
    public byte[] getPlainSign()
    {
        return _plainSign;
    }

    /**
     *
     * @return
     */
    @Override
    public byte[] getEasySign()
    {
        return _easySign;
    }
}
