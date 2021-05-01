/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testmanagment;

import database.DbManager;
import database.Student;
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
 * Pairs tests with students (used by teachers and allowed only if they teach these students)
 * @author Vavra
 */
public class EngageTest extends HttpServlet {

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
        Teacher t = new Teacher((User)request.getSession().getAttribute("user"), new ArrayList());
        t.loadClasses(db.getConnection());
        try (PreparedStatement insert = db.getConnection().prepareStatement("INSERT INTO " + db.getConnection().getCatalog() + ".`engaged_tests` (`test_id`, `user_id`, `begin_date`, `volunteer`, `term`) VALUES(?, ?, ?, ?, ?)")) {
            boolean volunteer = request.getParameter("volunteer") != null;
            for (Map.Entry<String, String[]> s : request.getParameterMap().entrySet()) {
                String[] first = s.getKey().split("/");
                if (first.length == 2) {
                    String[] student = first[0].split("-");
                    String[] test = first[1].split("-");
                    int studentId = Integer.parseInt(student[1]);
                    Student studentInstance = t.teachesStudent(studentId);
                    if(studentInstance == null){
                        continue;
                    }
                    if (student[0].equals("student") && test[0].equals("test") && s.getValue()[0].equals("on")) {
                        insert.setInt(1, Integer.parseInt(test[1]));
                        insert.setInt(2, studentId);
                        insert.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                        insert.setBoolean(4, volunteer);
                        insert.setInt(5, studentInstance.getClasss().getGrade());
                        insert.addBatch();
                    }
                }
            }
            insert.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("../Main/index.jsp");
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
