package com.stockly.android.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * CryptoUtil a class that provide method for password encryption using
 * RSA Golang algorithm using public key and
 * private key for decryption.
 */
public class CryptoUtil {

    private final static String CRYPTO_METHOD = "RSA";
    private final static int CRYPTO_BITS = 2048;
    private final static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhhTG+/piYXiz6yWYtFG9nJYymDbuMMu8UjiTT1dm4LzMa2B7K9SfnxGORqED3dl6o/EgVy1z0X2AgsbW/H7GqchP6hpMbG9rLUBgkhL/E+YJK40oLgmDRU52mDa0xnCuWGiBZC982F+z0KPomIxzQ/xb9hWAOlpRDETgv6mXL+kUo2HIDjZDYSGerGmlFJ85Ig46kTBIzMvWV41n7u4D3nWtpYR8TplpH6q5//LrOVyyFOffXUde4u/OsuHoqaxdcixGemLMMVkW9z/mPTWE5mhEqzZULagxVh9vas2YzRKleT+g7gZKUedxBATz8qLjDnnWIeud9WKLBA1Mmv7DVwIDAQAB";
    private final static String PRIVATE_KEY = "MIICWwIBAAKBgQDcNnuD9dT3N01h11HOQ9EYi5NJZ3AC62vdhTAzUYzb64OTU6hoTD9tGT+A+QHZgOpti5F6yKfEuscaCbUWpTh5FQcRS6dTiktwLvYK3VJjjqWNqsqzHlhIo2N2Lr4+IFatIqNrfpiaW6CZPi1xL37WV04FGv52pNtIYnqkrFqm3QIDAQABAoGALDuVW9BJUs788CY7/F6K5Y9Dqn7odO2s4PYb1HMRBCuuZi1rqmeGZfeoVdOul23ZqN9b/XnImS+bRpOkmEPfr7MwDvmur4IlmAuYpihJPz7J+2HFV4ofcBrvJi4qZeTGBKEubAEC8ZqZZIerQZwWJNJcD/qXDjA2JDipNWWHNwECQQDxMJz0GC5QSKZ8Ul8zBMJ/6McsejWMa5ZrWAIosOAXEtL25fGoSxmTnWie1Pic5SKN1KI+7jlbg25Rc8x+EqWtAkEA6bwevdFlxj21Xzy5Grflgp4Vr7IWvqwqqpAjQRT9SL3fyYMwkmoUAPkEQz+1piFE2gaagRIDbTJta4tG+a9L8QJAVoaBGBSAsO6PQ3RtkKj5edexdGRXR4vlR5coFx71FXH8WvS9EAn5H/rNaIWwtuCA1+7bUcY2r3zkG7Z2OxhHdQJAO9sYCEYqiQTegKAdSxFS7gsrxI2Xs4aFep5k2NLWNh2hxIobEzOlIcajZ9FV5FBawHa2dF03kWw7hzHooCJbEQJAC9x+IGkjflGcsndgidw8AUT7j7ga+z+Y68c445mcvCdw2GoWoqC/9qQNmgH+E1kt+NhDJFQo6nUhG+c/egprRg==";


    public static String encrypt(String plain)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, stringToPublicKey(PUBLIC_KEY));
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String result)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeySpecException,
            InvalidKeyException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, stringToPrivateKey(PRIVATE_KEY));
        byte[] decryptedBytes = cipher.doFinal(Base64.decode(result, Base64.DEFAULT));
        return new String(decryptedBytes);
    }

    private static PublicKey stringToPublicKey(String publicKeyString)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException {

        byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(CRYPTO_METHOD);
        return keyFactory.generatePublic(spec);
    }

    private static PrivateKey stringToPrivateKey(String privateKeyString)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException {

        byte[] pkcs8EncodedBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance(CRYPTO_METHOD);
        return kf.generatePrivate(keySpec);
    }
}