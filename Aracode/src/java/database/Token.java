/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import database.User;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.net.URLEncoder;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.Hasher;

/**
 * Fields and methods that RememberMeToken and ResetToken have in common
 *
 * @author Vavra
 */
public class Token {

    protected int userId;

    /**
     * token to be converted into hash
     */
    protected String token;
    protected byte[] hash;
    protected Timestamp expireDate;

    public Token(int userId, byte[] hash, Timestamp expireDate) {
        this.userId = userId;
        this.hash = hash;
        this.expireDate = expireDate;
    }

    public Token(int userId, int tokenLength, Timestamp expireDate) {
        this.userId = userId;
        this.token = generateToken(tokenLength);
        this.hash = Hasher.hash(token);
        this.expireDate = expireDate;
    }

    /**
     * generates string to be converted into hash that contains only Base64 characters
     * @param tokenLength length() of string
     * @return token String
     */
    protected String generateToken(int tokenLength) {
        SecureRandom random = new SecureRandom();
        byte[] tokenArray = new byte[tokenLength];
        random.nextBytes(tokenArray);
        return Base64.getEncoder().encodeToString(tokenArray);
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getHash() {
        return hash;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    /**
     *
     * @return true if token is not expired
     */
    public boolean isValid() {
        return this.expireDate.after(Calendar.getInstance().getTime());
    }
}
