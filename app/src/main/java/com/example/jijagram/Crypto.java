package com.example.jijagram;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String pass = "okeyboomer";
    private static SecretKeySpec keySpec;

    static {
        try {
            MessageDigest md =  MessageDigest.getInstance("SHA-256");
            byte[] bytes = pass.getBytes();
            md.update(bytes);
            byte[] key = md.digest(); // хэш
            keySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String text) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = aesCipher.doFinal(text.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String encoded) throws Exception {
        byte[] encrypted = Base64.decode(encoded, Base64.DEFAULT);
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = aesCipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }
}
