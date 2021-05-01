/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testmanagment;

import database.DbManager;
import database.EngagedTest;
import database.Teacher;
import database.User;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adds deadline to the Engaged test
 * @author Vavra
 */
public class RunTest extends HttpServlet {

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
        DbManager db = new DbManager();
        User user = (User) request.getSession().getAttribute("user");
        try (PreparedStatement update = db.getConnection().prepareStatement("UPDATE " + db.getConnection().getCatalog() + ".`engaged_tests` SET `deadline` = ? WHERE (`test_id` = ?) and (`user_id` = ?);")) {
            if (user.getCategory() == 1) {
                Teacher t = new Teacher(user, new ArrayList());
                t.loadClasses(db.getConnection());
                for (Map.Entry<String, String[]> s : request.getParameterMap().entrySet()) {
                    String[] first = s.getKey().split("/");
                    if (first.length == 2) {
                        String[] student = first[0].split("-");
                        String[] testId = first[1].split("-");
                        int studentId = Integer.parseInt(student[1]);
                        EngagedTest test = EngagedTest.retrieveById(Integer.parseInt(testId[1]), studentId, db.getConnection());
                        if (test.getDeadline() == null) {
                            if (t.teachesStudent(studentId) == null) {
                                continue;
                            }
                            if (student[0].equals("student") && testId[0].equals("test") && s.getValue()[0].equals("on")) {
                                update.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis() + test.getTimeToComplete() * 1000));
                                update.setInt(2, test.getId());
                                update.setInt(3, studentId);
                                update.addBatch();
                            }
                        }
                        update.executeBatch();
                    }
                }
            } else if (user.getCategory() == 2) {
                int testId = Integer.parseInt(request.getParameter("test"));
                EngagedTest test = EngagedTest.retrieveById(testId, user.getId(), db.getConnection());
                if (test.getDeadline() == null) {
                    update.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis() + test.getTimeToComplete() * 1000));
                    update.setInt(2, test.getId());
                    update.setInt(3, user.getId());
                    update.executeUpdate();
                    response.sendRedirect("/Main/test.jsp?test=" + testId);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/Main/index.jsp");
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
