package com.example.hourlymaids.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class AsymmetricCryptography {
    /**
     * Gets private.
     *
     * @param filename the filename
     * @return private
     * @throws Exception the exception
     * @desctiption get Private key from file
     */
    //https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * Gets public.
     *
     * @param filename the filename
     * @return public
     * @throws Exception the exception
     * @desctiption get Public key from file
     */
    //https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * Encrypt file.
     *
     * @param input  the input
     * @param output the output
     * @param key    the key
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     * @desctiption encrypt to file
     */
    public void encryptFile(byte[] input, File output, PublicKey key) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, cipher.doFinal(input));
    }

    /**
     * Decrypt file.
     *
     * @param input  the input
     * @param output the output
     * @param key    the key
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     * @desctiption decrypt to file
     */
    public void decryptFile(byte[] input, File output, PrivateKey key) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        writeToFile(output, cipher.doFinal(input));
    }

    /**
     * @param output
     * @param toWrite
     * @throws IOException
     * @desctiption write to file
     */
    private void writeToFile(File output, byte[] toWrite) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    /**
     * Encrypt text string.
     *
     * @param msg the msg
     * @param key the key
     * @return string
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException       the bad padding exception
     * @throws NoSuchPaddingException    the no such padding exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws InvalidKeyException       the invalid key exception
     * @desctiption encrypt to text
     */
    public String encryptText(String msg, PublicKey key)
            throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypt text string.
     *
     * @param msg the msg
     * @param key the key
     * @return string
     * @throws InvalidKeyException       the invalid key exception
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws BadPaddingException       the bad padding exception
     * @throws NoSuchPaddingException    the no such padding exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @desctiption decrypt to text
     */
    public String decryptText(String msg, PrivateKey key)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException,
            NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
    }

    /**
     * Get file in bytes byte [ ].
     *
     * @param f the f
     * @return byte [ ]
     * @throws IOException the io exception
     * @desctiption get file in bytes
     */
    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }
}
