<%-- 
    Document   : test
    Created on : 23.4.2020, 22:52:54
    Author     : Vavra
--%>

<%@page import="database.Option"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="database.Question"%>
<%@page import="database.EngagedTest"%>
<%@page import="database.DbManager"%>
<%@page import="database.Test"%>
<%@page import="database.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>ARACODE</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="../lib/font-awesome-4.7.0/css/font-awesome.min.css">

        <link rel="stylesheet" type="text/css" href ="CSS/sheets.css">
        <link rel="stylesheet" type="text/css" href="CSS/test.css">
        <link rel="icon" href="images/logoTransparent.png">
        <!--<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet">-->

        <script src="../lib/jquery/jquery-3.2.1.min.js"></script>
        <script src="../lib/bootstrap/js/popper.js"></script>
        <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="JS/TestCreatingManager.js"></script>
        <script src="JS/TestEngagingManager.js"></script>
        <script src="JS/MainJS.js" async defer></script>
        <script src="JS/TestWrite.js"></script>
    </head>
    <body>
        <%
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            User user = (User) session.getAttribute("user");
            DbManager db = new DbManager();
            String testId = request.getParameter("test");
            if (testId == null) {
                response.sendRedirect("/Login/login.jsp");
                return;
            }
            EngagedTest test = EngagedTest.retrieveById(Integer.parseInt(testId), user.getId(), db.getConnection());
            String revealSolution = request.getParameter("revealSolution");
            if (revealSolution == null && (new Timestamp(new Date().getTime()).after(test.getDeadline()) || !test.isAlterable())) {
                test.setAlterable(false, db.getConnection());
                response.sendRedirect("/Main/index.jsp");
                return;
            }
            out.print("<input type=\"hidden\" id=\"deadline\" value=\"" + test.getDeadline().getTime() + "\"></input>");
            test.loadQuestions(db.getConnection());
            test.loadAnswers(db.getConnection());
            if(revealSolution != null){
                test.loadReachedPoints(db.getConnection());
            }
        %>
        <form action="/SaveTestResults" method="POST">
            <%out.print("<input type=\"hidden\" name=\"testId\" value=\"" + test.getId() + "\">");%>
            <nav class="px-0 fixed-top" >
                <div class="px-3 container-fluid ">
                    <div class="links">
                        <%
                            int numberOfQuestions = test.getQuestions().size();
                            for (int i = 1; i <= numberOfQuestions; i++) {
                                out.print("<a ");
                                Question q = test.getQuestions().get(i - 1);
                                if (q.getType() == 0) {
                                    if (q.getOptions().get(0).getSelectedOption() != -1) {
                                        out.print("style=\"color:green;\" ");
                                    }
                                } else if (q.getType() == 1) {
                                    for (Option o : q.getOptions()) {
                                        if (o.getSelectedOption() != -1) {
                                            out.print("style=\"color:green;\" ");
                                        }
                                    }
                                } else if (q.getType() == 2 || q.getType() == 3) {
                                    int color = 0;
                                    bigger: for (Option o : q.getOptions()) {
                                        if (o.getSelectedOption() == -1) {
                                            color = 1;
                                        }
                                        for (Option oo : q.getOptions()) {
                                            if (o.getSubquestionNumber() != oo.getSubquestionNumber() && o.getSelectedOption() != -1 && o.getSelectedOption() == oo.getSelectedOption()) {
                                                color = 2;
                                                break bigger;
                                            }
                                        }
                                    }
                                    if (color == 0) {
                                        out.print("style=\"color:green;\" ");
                                    } else if(color == 2){
                                        out.print("style=\"color:red;\" ");
                                    }
                                }

                                out.print("class=\"refferencer\" id=\"refferencer" + i + "\"href=\"#question" + i + "\">" + i + "</a>");
                            }
                        %>
                        <div id="right-side-navbar" class="">
                            <div id="clock">

                            </div>
                            <a data-toggle="tooltip" data-placement="bottom" title="Uložit aktuální odpovědi" onclick="$(this).closest('form').submit()" href="#">
                                <img src="images/cloud-computing.png">
                            </a>
                        </div>
                    </div>
                </div>
            </nav>
            <section>
                <div class="px-3 container-fluid">
                    <%
                        List<Question> questions = test.getQuestions();
                        for (int i = 0; i < questions.size(); i++) {
                            out.print(questions.get(i).questionHTML(i + 1));
                        }
                    %>
                </div>          
                <input type="submit" name="set-unalterable" value="Odeslat test" class="button-submit btn btn-success btn-lg btn-block mt-2">
            </section>
        </form>



        <footer>
            <div class="container">
                <div class="text-center py-3">
                    Dotazy a hlášení o chybách směrujte na adresu: <a href="mailto:programovaninaarabske@gmail.com">programovaninaarabske@gmail.com</a>
                </div>
                <ul class="list-unstyled list-inline text-center">
                    <li class="list-inline-item">
                        <a class="mx-1" href="#" target="_blank">
                            <img src="images/logoTransparent.png" class="footer-icon index-icon">
                        </a>
                    </li>
                    <li class="list-inline-item">
                        <a class="mx-1" href="https://www.gyarab.cz/" target="_blank">
                            <img src="images/GALOGO.png" class="footer-icon index-icon">
                        </a>
                    </li>
                    <li class="list-inline-item">
                        <a class="mx-1" href="https://www.youtube.com/channel/UCFN6BbC7LGYRyzpeJPJzr4g?view_as=subscriber" target="_blank">
                            <img src="images/kisspng-united-states-youtube-logo-youtube-play-button-transparent-png-5ab1be08946c16.888989591521597960608.png" class="footer-icon index-icon">
                        </a><!--https://de.cleanpng.com/png-d125d3/download-png.html-->
                    </li>
                    <li class="list-inline-item">
                        <a class="mx-1" href="https://www.facebook.com/Programov%C3%A1n%C3%AD-na-Arabsk%C3%A9-255792351107794/" target="_blank">
                            <img src="images/fb_icon_325x325.png" class="footer-icon index-icon">
                        </a>
                    </li>
                </ul>
            </div>
            <div class="footer-copyright text-center py-3">© 2020 Copyright: Vladimír Vávra
            </div>
        </footer>
    </body>
</html>
