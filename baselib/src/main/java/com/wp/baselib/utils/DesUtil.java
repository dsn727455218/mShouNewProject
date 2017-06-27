package com.wp.baselib.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 数据加密解密工具
 * @author summer
 */
public class DesUtil {  
//    private static byte[] iv = {1,2,3,4,5,6,7,8};  
    private final static String transformation = "DES/ECB/PKCS5Padding";
    /**
     * 加密字符串
     * @param encryptString 待加密的字符串
     * @param encryptKey 加密key
     * @return
     * @throws Exception
     */
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);  
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());  
        return Base64.encode(encryptedData);  
    }  
    
    /**
     * 解密字符串 
     * @param decryptString 带解密的字符串
     * @param decryptKey 加密key
     * @return
     * @throws Exception
     */
    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi = Base64.decode(decryptString);  
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);  
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte decryptedData[] = cipher.doFinal(byteMi);  
       
        return new String(decryptedData);
    }  
    
    /**
	 * 通过MD5加密
	 * @param str
	 * @throws NoSuchAlgorithmException
	 */
	public static String MD5(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] oldBytes = str.getBytes(); 
		md.update(oldBytes, 0, oldBytes.length);
		byte[] bytes = md.digest();
		int i;
		StringBuilder buf = new StringBuilder("");
        for (byte aByte : bytes) {
            i = aByte;
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();  //32位
//        return buf.toString().substring(0, 24); 24位

    }
}  