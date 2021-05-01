/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import login.Hasher.HashContainer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.Hasher;
import login.Mail;
import org.json.simple.JSONObject;

/**
 *
 * @author Vavra
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private HashContainer hContainer;
    private int category;
    public static final int ADMIN_CATEGORY = 0;

    /**
     * Creates object of user with these values
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param email
     * @param hContainer
     * @param category 0 - Admin, 1 - Teacher, 2 - Student
     */
    public User(int id, String firstName, String lastName, String email, HashContainer hContainer, int category) {
        initUser(id, firstName, lastName, email, hContainer, category);
    }

    /**
     * Creates object of user with this email
     *
     * @param con Connection to database
     * @param email Email of user
     */
    public User(Connection con, String email) {
        retrieveUser(con, email);
    }

    /**
     * Creates object of user with this ID
     *
     * @param con Connection to database
     * @param id Id of user
     */
    public User(Connection con, int id) {
        retrieveUser(con, id);
    }

    /**
     * Creates object of user who wants to reset their password by reset token
     * from e-mail
     *
     * @param con Connection to database
     * @param token ResetToken value
     * @param dummy nothing important, it is used to make a difference between
     * constructors
     */
    public User(Connection con, String token, boolean dummy) {
        retrieveUserWithResetToken(con, token);
    }

    private void initUser(int id, String firstName, String lastName, String email, HashContainer hContainer, int category) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hContainer = hContainer;
        this.category = category;
    }

    @Override
    public String toString() {
        return "[id = " + id + ", firstName = " + firstName + ", lastName = " + lastName + ", e-mail = " + email
                + ", iterationCount = " + hContainer == null ? null : (hContainer.iterationCount + ", salt = " + Arrays.toString(hContainer.salt) + ", hash = " + Arrays.toString(hContainer.hash)) + "]";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public HashContainer getHashContainer() {
        return hContainer;
    }

    public int getCategory() {
        return category;
    }

    public void setHContainer(HashContainer hContainer) {
        this.hContainer = hContainer;
    }

    private User retrieveUserWithResetToken(Connection con, String token) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`forgot_password` WHERE `token`=?;");) {
            byte[] hash = Hasher.hash(token);
            statement.setBytes(1, hash);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                ResetToken tok = new ResetToken(r.getInt("user_id"), r.getBytes("token"), r.getTimestamp("token_expire"));
                try (PreparedStatement removeToken = con.prepareStatement("DELETE FROM " + con.getCatalog() + ".`forgot_password` WHERE `user_id`=?;")) {
                    removeToken.setInt(1, tok.getUserId());
                    removeToken.executeUpdate();
                }
                if (tok.isValid()) {
                    retrieveUser(con, tok.getUserId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds this user to database
     *
     * @param con Connection to database
     * @return
     */
    public boolean addUserToDatabase(Connection con) {
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT INTO `" + con.getCatalog() + "`.`users` (`first_name`, `last_name`, `email`, `iterationCount`, `salt`, `hash`, `category`) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
            statement.setString(1, this.getFirstName());
            statement.setString(2, this.getLastName());
            statement.setString(3, this.getEmail());
            statement.setInt(4, this.getHashContainer().iterationCount);
            statement.setBytes(5, this.getHashContainer().salt);
            statement.setBytes(6, this.getHashContainer().hash);
            statement.setInt(7, this.getCategory());
            statement.execute();
            return true;
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return false;
    }

    private void retrieveUser(Connection con, String email) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE email=?;")) {
            statement.setString(1, email);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                initUser(r.getInt("id"), r.getString("first_name"), r.getString("last_name"), r.getString("email"), new HashContainer(r.getInt("iterationCount"), r.getBytes("salt"), r.getBytes("hash")), r.getInt("category"));
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    private void retrieveUser(Connection con, int id) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`users` WHERE id=?;");) {
            statement.setInt(1, id);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                initUser(r.getInt("id"), r.getString("first_name"), r.getString("last_name"), r.getString("email"), new HashContainer(r.getInt("iterationCount"), r.getBytes("salt"), r.getBytes("hash")), r.getInt("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alters the database password information
     *
     * @param con Connection to database
     * @param hash Password information
     * @return true if password was changed, false if there was SQL exception
     */
    public boolean changePassword(Connection con, HashContainer hash) {
        try (PreparedStatement statement = con.prepareStatement("UPDATE " + con.getCatalog() + ".`users` SET `iterationCount` = ?, `salt` = ?, `hash` = ? WHERE (`id` = ?);")) {
            statement.setInt(1, hash.iterationCount);
            statement.setBytes(2, hash.salt);
            statement.setBytes(3, hash.hash);
            statement.setInt(4, id);
            statement.execute();
            return true;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Tries to change the database user's password and returns results
     *
     * @param con Connection to database
     * @param oldPass Password stored in database
     * @param newPass New password
     * @return HashContainer of new password if change has been successfull and
     * null if not
     */
    public HashContainer changePassword(Connection con, String oldPass, String newPass) {
        User databaseUser = new User(con, this.id);
        byte[] submittedOldHash = Hasher.hash(databaseUser.getHashContainer().iterationCount, databaseUser.getHashContainer().salt, oldPass, databaseUser.getHashContainer().hash.length * 8);
        if (slowEquals(databaseUser.getHashContainer().hash, submittedOldHash)) {
            HashContainer hash = Hasher.newHash(newPass);
            if (this.changePassword(con, hash)) {
                return hash;
            }
        }
        return null;
    }

    /**
     * Makes an attempt to verify log-in
     *
     * @param password
     * @return true if password is the same as the password stored in database
     */
    public boolean authenticate(String password) {
        byte[] newHash = Hasher.hash(this.getHashContainer().iterationCount, this.getHashContainer().salt, password, this.getHashContainer().salt.length * 8);
        return slowEquals(this.getHashContainer().hash, newHash);
    }

    /**
     * This method removes thread of timing attack, when hacker can quess part
     * of the password according to response time. for example comparing of
     * aaaaaaaaaa and baaaaaaaa is fasteer than comparing of aaaaaaaaaaaa and
     * aaaaaaaaaab. This method compares all hashes in same time
     *
     * @param databaseHash hash stored in database
     * @param newHash hash made from sent password
     * @return true if these hashes are equal
     */
    private boolean slowEquals(byte[] databaseHash, byte[] newHash) {
        boolean equals = true;
        int smaller = Math.min(databaseHash.length, newHash.length);
        if (databaseHash.length != newHash.length) {
            equals = false;
        }
        for (int i = 0; i < smaller; i++) {
            if (databaseHash[i] != newHash[i]) {
                equals = false;
            }
        }
        return equals;
    }

    /**
     * Creates ResetToken and tries to put it into the database. If it was
     * successfull it sends an e-mail containing reset value. User can click on
     * it and they will enter new password
     *
     * @param con Connection to the database
     * @return true if token has been created and sent by e-mail.
     */
    public boolean forgotPassword(Connection con) {
        ResetToken token = new ResetToken(this.getEmail(), con);
        if (token.putTokenToDatabase(con)) {
            Mail.sendEmail(this.getEmail(), token.getUrlToken());
            return true;
        }
        return false;
    }
}
