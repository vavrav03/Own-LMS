/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import database.DbManager;
import database.RememberMeToken;
import database.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vavra
 */
public class LoginController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String email = request.getParameter("email");
            String password = request.getParameter("pass");
            DbManager manager = new DbManager();
            User user = new User(manager.getConnection(), email);
            if (user.authenticate(password)) {
                if (request.getParameter("remember-me") != null) {
                    RememberMeToken token = new RememberMeToken(user.getId());
                    token.putRememberTokenToDatabase(manager.getConnection());
                    manager.closeConnection();
                    Cookie cookieSelector = new Cookie("selector", token.getSelector());
                    cookieSelector.setMaxAge(302_400);
                    response.addCookie(cookieSelector);
                    Cookie cookieToken = new Cookie("token", token.getToken());
                    cookieToken.setMaxAge(302_400);
                    response.addCookie(cookieToken);
                }
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("../Main/index.jsp");
            } else {
                request.setAttribute("wrongLogin", out);
                request.getAttribute("wrongLogin");
                response.sendRedirect("../Login/login.jsp?option=LoginFail");
            }
        }
    }

    /**
     * Checks if user has set RememberMeToken on. If yes, user object is created
     * and put to session
     *
     * @param request
     * @param response
     * @param session
     * @return true if user has RememberMeToken, false if not
     */
    public static boolean isLogged(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie selector = null;
            Cookie tokenValue = null;
            for (Cookie c : cookies) {
                if (c.getName().equals("selector")) {
                    selector = c;
                    c.setMaxAge(0);
                } else if (c.getName().equals("token")) {
                    tokenValue = c;
                    c.setMaxAge(0);
                }
            }
            if (selector != null && tokenValue != null) {
                DbManager manager = new DbManager();
                RememberMeToken token = RememberMeToken.retrieveRememberToken(manager.getConnection(), selector.getValue());
                if (token != null && Arrays.equals(token.getHash(), Hasher.hash(tokenValue.getValue()))) {
                    User user = new User(manager.getConnection(), token.getUserId());
                    session.setAttribute("user", user);

                    RememberMeToken newToken = new RememberMeToken(user.getId());
                    newToken.updateRememberToken(manager.getConnection());

                    selector.setValue(newToken.getSelector());
                    selector.setMaxAge(302_400);

                    tokenValue.setValue(newToken.getToken());
                    tokenValue.setMaxAge(302_400);
                    return true;
                }
                manager.closeConnection();
            }
        }
        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
