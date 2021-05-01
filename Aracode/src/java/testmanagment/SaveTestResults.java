/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testmanagment;

import database.DbManager;
import database.EngagedTest;
import database.Option;
import database.Option.ConnectOption;
import database.Option.FillInTextOption;
import database.Option.OrderOption;
import database.Question;
import database.User;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Saves results of test sent by student to the database
 *
 * @author Vavra
 */
public class SaveTestResults extends HttpServlet {

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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        DbManager db = new DbManager();
        EngagedTest test = EngagedTest.retrieveById(Integer.parseInt(request.getParameter("testId")), user.getId(), db.getConnection());
        test.loadQuestions(db.getConnection());
        Map<String, String[]> map = request.getParameterMap();
        test.setSubmitTime(new Timestamp(new Date().getTime()));
        test.checkDeadline(db.getConnection());
        if(!test.getIsAlterable()){
            response.sendRedirect("/Main/index.jsp");
            return;
        }
        int question = -1;
        int lastQuestion = -1;
        int subquestionNumber = 0;
        Question q = null;
        for (Map.Entry<String, String[]> item : map.entrySet()) {
            if (item.getKey().startsWith("question")) {
                String[] values = item.getKey().split("-");
                int questionId = Integer.parseInt(values[1]);
                if (questionId != lastQuestion) {
                    question++;
                    q = test.getQuestionById(question, questionId);
                    subquestionNumber = 0;
                }
                if (item.getValue().length > 0 && !item.getValue()[0].equals("dont-know")) {
                    switch (q.getType()) {
                        case 0:
                            q.getOptions().get(subquestionNumber).setSelectedOption(item.getValue()[0].equals("true") ? 1 : 0);
                            break;
                        case 1:
                            FillInTextOption of = (FillInTextOption) q.getOptions().get(subquestionNumber);
                            if (of.getRightWord().equals(item.getValue()[0])) {
                                of.setSelectedOption(0);
                            } else {
                                for (int i = 0; i < of.getWrongWords().size(); i++) {
                                    if (of.getWrongWords().get(i).equals(item.getValue()[0])) {
                                        of.setSelectedOption(i + 1);
                                    }
                                }
                            }
                            break;
                        case 2:
                            for (int i = 0; i < q.getOptions().size(); i++) {
                                ConnectOption oc = (ConnectOption) q.getOptions().get(i);
                                if (values[2].equals(oc.getDefinition())) {
                                    oc.setSelectedOption(Integer.parseInt(item.getValue()[0]));
                                }
                            }
                            break;
                        case 3:
                            for (int i = 0; i < q.getOptions().size(); i++) {
                                OrderOption oo = (OrderOption) q.getOptions().get(i);
                                if (values[2].equals(oo.getText())) {
                                    oo.setSelectedOption(Integer.parseInt(item.getValue()[0]));
                                }
                            }
                            break;
                    }
                }
                subquestionNumber++;
                lastQuestion = questionId;
            }
        }
//        for (Question questio : test.getQuestions()) {
//            for (Option o : questio.getOptions()) {
//                System.out.println("question: " + questio.getId() + ", option: " + o.getStudentAnswer());
//            }
//            System.out.println("");
//        }
        test.saveAnswers(db.getConnection());
        test.setAlterable(request.getParameter("set-unalterable") == null, db.getConnection());
        if (test.isAlterable()) {
            response.sendRedirect("/Main/test.jsp?test=" + test.getId());
        } else {
            response.sendRedirect("/Main/index.jsp");
        }
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
