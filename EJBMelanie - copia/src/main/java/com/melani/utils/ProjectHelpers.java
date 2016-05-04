package com.melani.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

public class ProjectHelpers {
    public static Logger LOGGER = Logger.getLogger(ProjectHelpers.class);
    private static final SecretKeySpec KEY = new SecretKeySpec("MyKey".getBytes(), "Blowfish");
    private static  Pattern pattern;
    private static  Matcher matcher;

    private ProjectHelpers() {
    }

    public static class ClaveSeguridad{

        public static String encriptar(String frase){
                            String encryptedRet="No Encrypted";
                            try {            
                                Cipher cipher = Cipher.getInstance("Blowfish");                                            
                                cipher.init(Cipher.ENCRYPT_MODE, KEY);            
                                byte[]encrypted=cipher.doFinal(frase.getBytes());            
                                encryptedRet=new String(encrypted);                          
                            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                                e.getLocalizedMessage();
                            }
                               return encryptedRet;
                                
                }

        public static String decriptar(String fraseEncriptada){
                                String decriptedRet="No decrypted";
                                try {
                                    
                                    Cipher cipher = Cipher.getInstance("Blowfish");                                    
                                    cipher.init(Cipher.DECRYPT_MODE, KEY); 
                                    byte[] encryptedData=fraseEncriptada.getBytes();       
                                    byte[] decrypted = cipher.doFinal(encryptedData);
                                    String afterEncryption = new String(decrypted);
                                    decriptedRet=afterEncryption;
                                     
                                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                                   
                                }
                    return decriptedRet;                                
        }   
        
            }

    public static class NombreUsuarioValidator{
        private static final String NAMEUSER_PATTERN="(?=^.{1,20}$)^([\\w\\.^\\-.][\\s]?)([\\w\\-\\s]*)([\\w]+$?)+$";            
        public static boolean validate(String nombreUsuario){              
                             pattern=Pattern.compile(NAMEUSER_PATTERN);
                             matcher = pattern.matcher(nombreUsuario);                  
                           return matcher.matches();
                        }
        
    }

    public static class PasswordValidator{
        private static final  String PASSWORD_PATTERN="^(?=.*[A-Z])(?=.*\\d)(?!.*(.)\\1\\1)[a-zA-Z0-9@]{4,20}$";
       
        public static boolean validate(String password){
                             pattern=Pattern.compile(PASSWORD_PATTERN);
                             matcher = pattern.matcher(password);                  
                           return matcher.matches();
                    }       
        }

    public static class NumeroDocumentoValidator{
                private static final  String NUMBER_PATTERN="(?=^.{1,10}$)\\d+$";
        public static boolean validate(String numerodocumento){
                                 pattern=Pattern.compile(NUMBER_PATTERN);
                                matcher = pattern.matcher(numerodocumento);                  
                        return matcher.matches();
        }
    }

    public static class NombreyApellidoValidator {
        private static final String NOMBREYAPELLIDO_PATTERN="(?=^.{1,30}$)[[A-Z][a-z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]* ?[[a-zA-Z]\\p{IsLatin}]+$";

        public static boolean validate(final String nombreyapellido){   
                              pattern=Pattern.compile(NOMBREYAPELLIDO_PATTERN);
                        matcher = pattern.matcher(nombreyapellido);                  
                        return matcher.matches();
                    }
    }

    public static class EmailValidator {
               private static final  String EMAIL_PATTERN="^[\\w\\-\\+\\*]+[\\w\\S]@(\\w+\\.)+[\\w]{2,4}$";

        public static boolean validate(String email){
                   pattern=Pattern.compile(EMAIL_PATTERN);
                            matcher = pattern.matcher(email);                  
                        return matcher.matches();
               }        
   }
    
    public static class TelefonoValidator{
        private static final String TELEFONO_PATTERN ="^(4|15)(\\d){6,}+$";
        private static final String PREFIJO_PATTERN ="^(\\d){1,8}+$";
        
        public static boolean validateTelefono(final String telefeno){
             pattern=Pattern.compile(TELEFONO_PATTERN);
	     matcher = pattern.matcher(telefeno);                  
          return matcher.matches();
        }
        
        public static boolean validatePrefijo(final String prefijo){
             pattern=Pattern.compile(PREFIJO_PATTERN);
	     matcher = pattern.matcher(prefijo);                  
          return matcher.matches();
        }    
    }
    public static String parsearCaracteresEspecialesXML(String xmlNota){        
        String xml;
        StringBuilder sb;
        String caracteresParseados = null;
            try {
                sb = new StringBuilder(10);
                        sb.append(xmlNota);
                    xml=StringEscapeUtils.escapeXml10(xmlNota.substring(xmlNota.indexOf("es>")+3,xmlNota.indexOf("</ob")));
                    sb.replace(sb.indexOf("es>")+3, sb.indexOf("</ob"), xml);
                     caracteresParseados = sb.toString();
            } catch (Exception e) {                
                LOGGER.error("Error en metodo parsearCaracteresEspecialesXML "+e.getMessage());
            }            
        return caracteresParseados;            
    }
    public static String parsearCaracteresEspecialesXML1(String xmlaParsear) {
        String xml = null;
        StringBuilder sb;
       
    try {
        sb=new StringBuilder(xmlaParsear);
            if(xmlaParsear.contains("<item>")){
                xml=StringEscapeUtils.escapeXml10(xmlaParsear.substring(xmlaParsear.indexOf("nes>")+4,xmlaParsear.indexOf("</obse")));                
                sb.replace(sb.indexOf("nes>")+4, sb.indexOf("</obse"), xml);                
            }
           if(xmlaParsear.contains("<Domicilio>")){
                xml=StringEscapeUtils.escapeXml10(xmlaParsear.substring(xmlaParsear.indexOf("mes>")+4,xmlaParsear.indexOf("</det1")));                
                sb.replace(sb.indexOf("mes>")+4, sb.indexOf("</det1"), xml);
           }
           xml=sb.toString();          
    } catch (Exception e) {
       
        LOGGER.error("Error en metodo parsearCaracteresEspecialesXML1 "+e.getLocalizedMessage());
    }
        return xml;    
    }
}