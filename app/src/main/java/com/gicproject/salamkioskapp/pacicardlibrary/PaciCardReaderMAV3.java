package com.gicproject.salamkioskapp.pacicardlibrary;


import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

import com.identive.libs.SCard;
import com.telpo.tps550.api.reader.SmartCardReader;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class PaciCardReaderMAV3 extends PaciCardReaderAbstract {

    SCard reader;
    UsbDeviceConnection connection;
    UsbEndpoint endPointOut;
    UsbEndpoint endPointIn;
    public PaciCardReaderMAV3(boolean var1, SCard mReader) {
        super(var1,mReader);
        this.AID = DataConstants.MAV3_AID;
        this.GemAID = DataConstants.MAV3_GEM_AID;
        this.reader = mReader;
        this.schema = new SchemaContentMAV3("");
        this.CetificateMatchString = "\\bThe Public Authority for Civil Information ID\\b";
        this.AOID = "3f0052005003";
        super.CardEFCertificatePath = "3f005200B000";
        this.AllAOID.add("3f0051005003");
    }

    public void Dispose() {
        if (!this.Disposed) {
         //   this.Dispose();
        }

    }

    private byte[] sendApdu(byte[] data) throws Exception {


        SCard.SCardIOBuffer transmit = reader.new SCardIOBuffer();
        transmit.setnInBufferSize(data.length);
        transmit.setAbyInBuffer(data);
        transmit.setnOutBufferSize(0x8000);
        transmit.setAbyOutBuffer(new byte[0x8000]);
        Long status1 = reader.SCardTransmit(transmit);
        Log.d(
                "TAG",
                "onClick:resul " + data + "---" + data.length
        );
        String rstr = "";
        String sstr = "";

        for(int i = 0; i < transmit.getnBytesReturned(); i++){
            int temp = transmit.getAbyOutBuffer()[i] & 0xFF;
            if(temp < 16){
                rstr = rstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) ;
                sstr = sstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) + " ";
            }else{
                rstr = rstr.toUpperCase() + Integer.toHexString(temp) ;
                sstr = sstr.toUpperCase() + Integer.toHexString(temp) + " " ;
            }
        }
        Log.d("TAG", "onClick:result " + rstr);
        Log.d("TAG", "onClick:result1 " + sstr);
        for(int i = 0; i < transmit.getnBytesReturned(); i++){
            int temp = transmit.getAbyOutBuffer()[i] & 0xFF;
            if(temp < 16){
                rstr = rstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) ;
                sstr = sstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) + " ";
            }else{
                rstr = rstr.toUpperCase() + Integer.toHexString(temp) ;
                sstr = sstr.toUpperCase() + Integer.toHexString(temp) + " " ;
            }
        }
        Log.d("TAG", "onClick:result " + rstr);
        Log.d("TAG", "onClick:result1 " + sstr);
        return  hexToByteArray(sstr);
    }

    public static byte[] hexToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    | Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }





    byte[] ReadSpecificObject(String var1, ModelDataLocation var2) throws Exception {
      /*  PACICardCommunicatorInterface var3 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        if (!var3.IsConnected()) {
            var3.Connect();
        }
*/
        this.SelectAIDAndFile(var1, var2.EfPath);
        int var4 = var2.Offset;
        int var5 = var2.Length;
        if (var5 < 252) {
            return this.ReadBinary(var1, var4, var5);
        } else {
            byte[] var6 = new byte[var5];
            int var7 = 0;

            while(var7 < var5) {
                if (var7 + 252 < var5) {
                    System.arraycopy(this.ReadBinary(var1, var4 + var7, 252), 0, var6, var7, 252);
                    var7 += 252;
                } else {
                    System.arraycopy(this.ReadBinary(var1, var4 + var7, var5 - var7), 0, var6, var7, var5 - var7);
                    var7 = var5;
                }
            }

            return var6;
        }
    }

    public PACICardProperties GetCardProperties(String var1) throws PaciException {
        PACICardProperties var2 = new PACICardProperties();
        var2.setCardEdition(3);
        X509Certificate var3 = this.FindCardCertificate(var1);
        if (var3 != null) {
            RSAPublicKey var4 = (RSAPublicKey)var3.getPublicKey();
            var2.setKeySize(var4.getModulus().bitLength());
        } else {
            var2.setKeySize(0);
        }

        int var6 = this.PINAttemptsStatus(var1);
        if (var6 <= 0) {
            var2.setCardLocked(true);
            var2.setAvailablePinAttempts(0);
        } else {
            var2.setCardLocked(false);
            var2.setAvailablePinAttempts(var6);
        }

        X509Certificate var5 = this.FindDigitalSignatureCertificate(var1);
        if (var5 != null) {
            var2.setDigitalSignatureAvailable(true);
        }

        return var2;
    }

    public int PINAttemptsStatus(String var1) throws PaciException {
        try {
         //   PACICardCommunicatorInterface var2 = (PACICardCommunicatorInterface)this.Cards.get(var1);
        //    if (!var2.IsConnected()) {
            //    var2.Connect();
        //    }

         //   if (!var2.IsConnected()) {
           //     throw new PaciException("Requested card was not found");
         //   } else {
            //    var2.BeginTransaction();
            ModelAPDUResponse var3 = new ModelAPDUResponse(sendApdu(DataConstants.MAV3_GEM_AID));
              //  ModelAPDUResponse var3 = new ModelAPDUResponse(reader.transmit(DataConstants.MAV3_GEM_AID));
               // ModelAPDUResponse var3 = var2.SendAPDU(DataConstants.MAV3_GEM_AID);
                if (!var3.ResponseOK()) {
             //       var2.EndTransaction(0);
                    throw new PaciException("Card internal application could not be selected");
                } else {
                    byte[] var4 = new byte[]{0, 32, 0, 17, 0};

                     var3 = new ModelAPDUResponse(sendApdu(var4));
                 //    var3 = new ModelAPDUResponse(reader.transmit(var4));

                   // var3 = var2.SendAPDU(var4);
                    if (var3.SW1 != 99) {
                    //    var2.EndTransaction(0);
                        throw new PaciException("Card internal file could not be selected");
                    } else {
                     //   var2.EndTransaction(0);
                        return var3.SW2 & 15;
                    }
                }
          //  }
        } catch (PaciException var5) {
            throw new PaciException("Card internal file could not be selected");
        } catch (Exception e) {
            throw new PaciException("Send Apdu crashed");
        }
    }

    public ModelDataLocation[] GetAllCardCertificateLocationAndOffset(String var1) throws PaciException, UnsupportedEncodingException {
        ArrayList var2 = new ArrayList();
        ModelDataLocation var3 = new ModelDataLocation();
        var3.EfPath = "3f005200B000";
        var3.Length = 0;
        var3.Offset = 0;
        var2.add(var3);
        Iterator var4 = this.AllAOID.iterator();

        while(var4.hasNext()) {
            String var5 = (String)var4.next();

            try {
                ModelDataLocation[] var6 = this.GetCardCertificateLocationAndOffset(var1, var5);
                if (var6 != null) {
                    HashSet var7 = new HashSet(Arrays.asList(var6));
                    var2.addAll(var7);
                }
            } catch (PaciException var8) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (ModelDataLocation[])var2.toArray(new ModelDataLocation[var2.size()]);
    }
}
