/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

/**
 * Интрефейс(конструктор) подписи
 * @author maria
 */
public interface Signature {
    byte[] getPlainSign();
    byte[] getEasySign();
}
