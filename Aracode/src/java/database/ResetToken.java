/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * This class represents forgot_password table in database
 * @author Vavra
 */
public class ResetToken extends Token {

    private static final int TOKEN_LENGTH = 128;
    public static final int EXPIRE_TIME_MILIS = 1_800_000;

    public ResetToken(String email, Connection con) {
        super(new User(con, email).getId(), TOKEN_LENGTH, new Timestamp(Calendar.getInstance().getTimeInMillis() + EXPIRE_TIME_MILIS));
    }

    public ResetToken(int id, byte[] hash, Timestamp expireDate) {
        super(id, hash, expireDate);
    }

    public String getUrlToken(){
        try {
            return URLEncoder.encode(super.token, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return null;
    }

    /**
     * Adds or updates token in database (only 1 for user can be present)
     * @param con Connection to database
     * @return true if token has been added or updated
     */
    public boolean putTokenToDatabase(Connection con) {
        if (ResetToken.retrieveResetToken(con, userId) == null) {
            try (PreparedStatement statement = con.prepareStatement("INSERT INTO `" + con.getCatalog() + "`.`forgot_password` (`user_id`, `token`, `token_expire`) VALUES (?, ?, ?);")) {
                statement.setInt(1, this.getUserId());
                statement.setBytes(2, this.getHash());
                statement.setTimestamp(3, this.getExpireDate());
                statement.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (PreparedStatement statement = con.prepareStatement("UPDATE " + con.getCatalog() + ".`forgot_password` SET `token` = ?, `token_expire` = ? WHERE (`user_id` = ?);")) {
                statement.setBytes(1, this.getHash());
                statement.setTimestamp(2, this.getExpireDate());
                statement.setInt(3, this.getUserId());
                statement.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Mines ResetToken for given user from the database
     * @param con Connection to database
     * @param userId user who wants password reset
     * @return ResetToken contained in database. If not present, returns null
     */
    public static ResetToken retrieveResetToken(Connection con, int userId) {
        System.out.println(userId);
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`forgot_password` WHERE user_id=?;");) {
            statement.setInt(1, userId);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                return new ResetToken(userId, r.getBytes("token"), r.getTimestamp("token_expire"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
