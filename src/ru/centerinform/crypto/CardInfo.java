/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.centerinform.crypto;

/**
 * Информация о смарт-карте
 * @author maria
 */
public class CardInfo {
    private final String _label;
    private final long   _slotId;
        
    CardInfo(String label, long slotId)
    {
        _label = label;
        _slotId = slotId;
    }
        
    public String getLabel()
    {
        return _label; // серийный номер
    }
        
    public long getSlotId()
    {
        return _slotId; //слот
    }
        
    @Override
    public String toString()
    {
        return _label;
    }
}
