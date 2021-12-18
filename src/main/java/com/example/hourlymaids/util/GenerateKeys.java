package com.example.hourlymaids.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

/**
 * The type Generate keys.
 */
public class GenerateKeys {
    private KeyPairGenerator keyGen;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Instantiates a new Generate keys.
     *
     * @param keylength the keylength
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws NoSuchProviderException  the no such provider exception
     * @desctiption Constructor
     */
    public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keylength);
    }

    /**
     * Create keys.
     *
     * @desctiption create keys
     */
    public void createKeys() {
        KeyPair pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    /**
     * Gets private key.
     *
     * @return private key
     * @desctiption get private key
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Gets public key.
     *
     * @return public key
     * @desctiption get public key
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Write to file.
     *
     * @param path the path
     * @param key  the key
     * @throws IOException the io exception
     * @desctiption write to file
     */
    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    /**
     * Generate private public key.
     *
     * @param rootDir the root dir
     * @desctiption Genereate private and public key
     */
    public static void generatePrivatePublicKey(String rootDir) {
        GenerateKeys gk;
        try {
            gk = new GenerateKeys(1024);
            gk.createKeys();
            gk.writeToFile(rootDir + "/publicKey", gk.getPublicKey().getEncoded());
            gk.writeToFile(rootDir + "/privateKey", gk.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            e.printStackTrace();
        }
    }
}