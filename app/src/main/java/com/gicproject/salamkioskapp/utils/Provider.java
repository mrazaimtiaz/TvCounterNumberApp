package com.gicproject.salamkioskapp.utils;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;


import com.gicproject.salamkioskapp.emvnfccard.exception.CommunicationException;
import com.gicproject.salamkioskapp.emvnfccard.parser.IProvider;

import java.io.IOException;

import fr.devnied.bitlib.BytesUtils;

public class Provider implements IProvider {



    void constructor(){


    }
    private IsoDep mTagCom;
    public Provider(IsoDep TagCom) {
        mTagCom = TagCom; // Set the initial value for the class attribute x
    }

    @Override
    public byte[] transceive(final byte[] pCommand) throws CommunicationException {

        byte[] response;
        try {
            // send command to emv card
         //   mTagCom.connect();
            if (!mTagCom.isConnected()) {
                mTagCom.connect();
            }
            response = mTagCom.transceive(pCommand);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }

        return response;
    }

    @Override
    public byte[] getAt() {

        // For NFC-A
        return BytesUtils.fromString("00814D22088660300020E00001");
        // For NFC-B
        // return mTagCom.getHiLayerResponse();
    }


    public void setmTagCom(final IsoDep mTagCom) {
        this.mTagCom = mTagCom;
    }

}

