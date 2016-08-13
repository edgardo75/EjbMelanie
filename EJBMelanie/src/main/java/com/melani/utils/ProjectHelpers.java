package com.melani.utils;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class ProjectHelpers { 
    public String encryptionKey;
   
    public ProjectHelpers(){}
    public String parsearCaracteresEspecialesXML(String xmlNota){        
        String xml;
        StringBuilder sb;
        String caracteresParseados;            
                sb = new StringBuilder(32);
                        sb.append(xmlNota);
                    xml=xmlNota.substring(xmlNota.indexOf("es>")+3,xmlNota.indexOf("</ob"));
                    sb.replace(sb.indexOf("es>")+3, sb.indexOf("</ob"), xml);
                     caracteresParseados = sb.toString();
        return caracteresParseados;            
    }
    public String parsearCaracteresEspecialesXML1(String xmlaParsear) {
        String xml;
        StringBuilder sb;           
        sb=new StringBuilder(xmlaParsear);
            if(xmlaParsear.contains("<item>")){
                xml=xmlaParsear.substring(xmlaParsear.indexOf("nes>")+4,xmlaParsear.indexOf("</obse"));               
                sb.replace(sb.indexOf("nes>")+4, sb.indexOf("</obse"), xml);                
            }
           if(xmlaParsear.contains("<Domicilio>")){
                xml=xmlaParsear.substring(xmlaParsear.indexOf("mes>")+4,xmlaParsear.indexOf("</det1"));                
                sb.replace(sb.indexOf("mes>")+4, sb.indexOf("</det1"), xml);
           }
           xml=sb.toString();
           return xml;    
    }   

    
    
    public String encrypt(String value) {
    try {
        // Get the KeyGenerator
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(256);
        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded(); 
        String key = Base64.getEncoder().encodeToString(raw);
        this.encryptionKey = key;                        
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));        
        return encrypt;
    } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException ex) {
        Logger.getLogger(ProjectHelpers.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "No Encrypted";
}
    
    public String decrypt(String key, String encrypted) {
    try {
        Key k = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] decodedValue = Base64.getDecoder().decode(encrypted);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
        Logger.getLogger(ProjectHelpers.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "No Decrypted";
}
    }