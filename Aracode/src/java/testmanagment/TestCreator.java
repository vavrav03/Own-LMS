/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testmanagment;

import database.DbManager;
import database.Option.BooleanOption;
import database.Option.ConnectOption;
import database.Option.FillInTextOption;
import database.Option.OrderOption;
import database.Question;
import database.Test;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Stores created test in database. Data validation present here or in
 * constructors of constructed objects
 *
 * @author Vavra
 */
public class TestCreator extends HttpServlet {

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
        Test test = new Test(request.getParameter("test-name"),
                Integer.parseInt(request.getParameter("grade")),
                Integer.parseInt(request.getParameter("days")),
                Integer.parseInt(request.getParameter("hours")),
                Integer.parseInt(request.getParameter("minutes")),
                Integer.parseInt(request.getParameter("seconds")),
                request.getParameter("practice") != null);
        int questionNumber = 0;
        try {
            while (true) {
                Question question = new Question(request.getParameter("question-text" + questionNumber),
                        request.getParameter("right-option-points" + questionNumber),
                        request.getParameter("wrong-option-points" + questionNumber),
                        request.getParameter("question-type-selector" + questionNumber));
                switch (question.getType()) {
                    case 0:
                        String regex = "radio.*" + questionNumber;
                        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
                            if (Pattern.matches(regex, param.getKey())) {
                                question.addOption(new BooleanOption(param.getValue()[0].equals("true")));
                            }
                        }
                        break;
                    case 1:
                        int rightOptionNumber = 0;
                        FillInTextOption option = null;
                        String rightOption = null;
                        while ((rightOption = request.getParameter("right-option" + questionNumber + rightOptionNumber)) != null) {
                            String wrongOption = null;
                            int wrongOptionNumber = 0;
                            option = new FillInTextOption(rightOptionNumber, new ArrayList(), rightOption);
                            while ((wrongOption = request.getParameter("wrong-option" + questionNumber + rightOptionNumber + wrongOptionNumber++)) != null) {
                                option.addWrongWord(wrongOption);
                            }
                            rightOptionNumber++;
                            question.addOption(option);
                        }
                        break;
                    case 2:
                        String word1 = null;
                        String definition = null;
                        int optionNumber1 = 0;
                        while ((word1 = request.getParameter("word" + questionNumber + optionNumber1)) != null && (definition = request.getParameter("definition" + questionNumber + optionNumber1)) != null) {
                            question.addOption(new ConnectOption(definition, optionNumber1++, word1));
                        }
                        break;

                    case 3:
                        String word = null;
                        int optionNumber = 0;
                        while ((word = request.getParameter("word" + questionNumber + optionNumber++)) != null) {
                            question.addOption(new OrderOption(optionNumber, word));
                        }
                }
                test.addQuestion(question);
                questionNumber++;
            }
        } catch (NullPointerException e) {
            DbManager db = new DbManager();
            test.addNewTest(db.getConnection(), request.getParameter("not-create-test") != null);
            db.closeConnection();
            response.sendRedirect("../Main/index.jsp");
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
