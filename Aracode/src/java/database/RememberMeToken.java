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
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author Vavra
 */
public class RememberMeToken extends Token {

    private int id;
    private String selector;
    private static final int TOKEN_LENGTH = 128;

    /**
     * maximum numbers of tokens for one user
     */
    public static final int MAX_TOKENS = 20;
    private static final int EXPIRE_TIME_MILIS = 604_800_000;

    public RememberMeToken(int id, int userId, String selector, byte[] hash, Timestamp tokenExpire) {
        super(userId, hash, tokenExpire);
        this.selector = selector;
        this.id = id;
    }

    public RememberMeToken(int userId) {
        super(userId, TOKEN_LENGTH, new Timestamp(Calendar.getInstance().getTimeInMillis() + EXPIRE_TIME_MILIS));
        this.selector = super.generateToken(TOKEN_LENGTH / 4);
    }

    public String getSelector() {
        return selector;
    }

    public int getId() {
        return id;
    }

    /**
     * Adds this token to the database
     * @param con Connection to database
     * @return true if token was added
     */
    public boolean putRememberTokenToDatabase(Connection con) {
        if (!canAddMoreRememberTokens(con, this.getUserId())) {
            throw new RuntimeException("moc tokenu");
        }
        try (PreparedStatement statement = con.prepareStatement("INSERT INTO `" + con.getCatalog() + "`.`remembered_users` (`selector`, `hash`, `user_id`, `token_expire`) VALUES (?, ?, ?, ?);")) {
            statement.setString(1, this.getSelector());
            statement.setBytes(2, this.getHash());
            statement.setInt(3, this.getUserId());
            statement.setTimestamp(4, this.getExpireDate());
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mines token from database with given selector
     * @param con Connection to the database
     * @param selector selector of token
     * @return token mined from database. If not present, returns null
     */
    public static RememberMeToken retrieveRememberToken(Connection con, String selector) {
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + con.getCatalog() + ".`remembered_users` WHERE selector=?;");) {
            statement.setString(1, selector);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                RememberMeToken token = new RememberMeToken(r.getInt("token_id"), r.getInt("user_id"), r.getString("selector"), r.getBytes("hash"), r.getTimestamp("token_expire"));
                return token.isValid() ? token : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates token with this token_id.
     * @param con Connection to the database
     * @return true if at least one token has been updated
     */
    public boolean updateRememberToken(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("UPDATE " + con.getCatalog() + ".`remembered_users` SET `selector` = ?, `hash` = ?, `token_expire` = ? WHERE (`token_id` = ?);");) {
            statement.setString(1, this.getSelector());
            statement.setBytes(2, this.getHash());
            statement.setTimestamp(3, this.getExpireDate());
            statement.setInt(4, this.getId());
            if (statement.executeUpdate() != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes token with passed selector from the database
     * @param con Connection to the database
     * @param selector Token with this selector shall be deleted
     * @return true if at least one token has been deleted
     */
    public static boolean deleteRememberMeToken(Connection con, String selector) {
        try (PreparedStatement statement = con.prepareStatement("DELETE FROM " + con.getCatalog() + ".`remembered_users` WHERE `selector`=?;");) {
            statement.setString(1, selector);
            if (statement.executeUpdate() != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Every user has limited number of tokens in database to prevent memory attacks on database
     * @param con Connection to the database
     * @param user ID of user for capacity check
     * @return true if there is capacity for at least one more token
     */
    private boolean canAddMoreRememberTokens(Connection con, int user) {
        try (PreparedStatement statement = con.prepareStatement("SELECT COUNT(`token_id`) AS total FROM " + con.getCatalog() + ".`remembered_users` WHERE `user_id`=?;");) {
            statement.setInt(1, user);
            try (ResultSet r = statement.executeQuery()) {
                r.next();
                return r.getInt("total") < RememberMeToken.MAX_TOKENS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
