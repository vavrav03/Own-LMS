/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Class for manipulation with hashes
 *
 * @author Vavra
 */
public class Hasher {

    private static int hashLength = 128;

    /**
     * Database information container
     */
    public static class HashContainer {

        public int iterationCount;
        public byte[] salt;
        public byte[] hash;

        public HashContainer(int iterationCount, byte[] salt, byte[] hash) {
            this.iterationCount = iterationCount;
            this.salt = salt;
            this.hash = hash;
        }
    }

    /**
     * Hashes password and provides info about it. Length of salt is hashLength
     * / 8, iterationCount lies between 10 000 and 100 000
     *
     * @param password password to be hashed
     * @return HashContainer of that password
     */
    public static HashContainer newHash(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[hashLength >>> 3];
        random.nextBytes(salt);
        int iterationCount = random.nextInt(90_000) + 10_000;
        byte[] hash = hash(iterationCount, salt, password, hashLength);
        return new HashContainer(iterationCount, salt, hash);
    }

    /**
     * Hashes password with given parameters
     * @param iterationCount
     * @param salt
     * @param password
     * @param hashLength
     * @return hash
     */
    public static byte[] hash(int iterationCount, byte[] salt, String password, int hashLength) {
        try {
            PBEKeySpec pasContainer = new PBEKeySpec(password.toCharArray(), salt, iterationCount, hashLength);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").
                    generateSecret(pasContainer).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
        } catch (InvalidKeySpecException ex) {
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Token hasher
     * @param token
     * @return hash of token
     */
    public static byte[] hash(String token) {
        return hash(1, new byte[]{1}, token, token.length());
    }

    /**
     * calls hash(int iterationCount, byte[] salt, String password, int hashLength) with hashLength = 128
     * @param iterationCount
     * @param hash
     * @param password
     * @return
     */
    public static byte[] hash(int iterationCount, byte[] hash, String password) {
        return hash(iterationCount, hash, password, hashLength);
    }
}
