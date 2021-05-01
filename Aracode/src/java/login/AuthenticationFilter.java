/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import database.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vavra
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/Main/index.jsp", "/LogoutController", "/EngageTest", "/RunTest", "/TestCreator", "/Main/test.jsp", "/SaveTestResults", "/ChangePasswordController", "/Main/test.jsp"})
public class AuthenticationFilter implements Filter {

    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public AuthenticationFilter() {
    }

    /**
     * If user attribute in session is null, then user is redirected to login
     * page, otherwise he can continue. If he has RememberMeToken, user is
     * created and he can also continue
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") == null) {
            if (LoginController.isLogged(req, res, session)) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect("../Login/login.jsp");
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
