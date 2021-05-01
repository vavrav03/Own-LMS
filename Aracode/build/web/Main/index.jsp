
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="database.EngagedTest"%>
<%@page import="database.Student"%>
<%@page import="database.Classs"%>
<%@page import="java.util.ArrayList"%>
<%@page import="database.Test"%>
<%@page import="database.DbManager"%>
<%@page import="database.User"%>
<%@page import="database.Teacher"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>ARACODE</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="../lib/font-awesome-4.7.0/css/font-awesome.min.css">

        <link rel="stylesheet" type="text/css" href ="CSS/sheets.css">
        <link rel="icon" href="images/logoTransparent.png">
        <!--<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet">-->

        <script src="../lib/jquery/jquery-3.2.1.min.js"></script>
        <script src="../lib/bootstrap/js/popper.js"></script>
        <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="JS/TestCreatingManager.js"></script>
        <script src="JS/TestEngagingManager.js"></script>
        <script src="JS/MainJS.js" async defer></script>

        <!--old version - links-->
        <!--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous"> -->
        <!--<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>-->
        <!--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>-->
        <!--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>-->
    </head>

    <body>
        <%
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            //this forbids to click back button after signing out. 
            User user = (User) session.getAttribute("user");
        %>
        <c:choose>
            <c:when test="${user.getCategory() == User.ADMIN_CATEGORY}">
                <c:set var="firstLink" value=""></c:set>
                <c:set var="secondLink" value=""></c:set>
                <c:set var="firstSection">
                    Admin
                </c:set>
            </c:when>
            <c:when test="${user.getCategory() == Teacher.CATEGORY}">
                <c:set var="firstLink" value="Testy"></c:set>
                <c:set var="secondLink" value="Klasifikace"></c:set>
                <c:set var="firstSection">
                    <div class="px-0 navbar navbar-expand-lg navbar-light" id="test-navbar">
                        <div class="px-0 container-fluid">
                            <a class="mb-1 navbar-brand" href="#">Testy</a>
                            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#test-nav" aria-controls="test-nav" aria-expanded="false" aria-label="Test navigation">
                                <span class="navbar-toggler-icon"></span>
                            </button>
                            <div class="collapse navbar-collapse" id="test-nav">
                                <div class="navbar-nav">
                                    <a class="nav-item nav-link test-nav-item" href="#" onclick="newTest()">Nový test</a>
                                    <a class="nav-item nav-link test-nav-item" href="#" onclick="assignTest()">Přidělit test</a>
                                    <a class="nav-item nav-link test-nav-item" href="#" onclick="runTest()">Spustit přidělený test</a>
                                    <a class="nav-item nav-link test-nav-item" href="#" onclick="alterTest()">Upravit test</a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="test-div" id="new-test">
                        <form action="/TestCreator" method="get" class="test-creator-content">
                            <div class="test-headding rounded p-1 mb-3">
                                <textarea class="form-control mb-1" rows="1" name="test-name" placeholder="Jméno testu" required maxlength="60"></textarea>
                                <div class="form-group row m-0">
                                    <input class="form-control col-sm-5 mb-1 mb-sm-0 " type="number" name="grade" placeholder="Ročník" required min="1" max="256"></textarea>
                                    <div class="row col-12 col-sm-6 ml-auto mr-0 px-0 pull-right">
                                        <input class="form-control col-3" type="number" name="days" placeholder="Dny" min="0" max="365" required>
                                        <input class="form-control col-3" type="number" name="hours" placeholder="Hod" min="0" max="23" required>
                                        <input class="form-control col-3" type="number" name="minutes" placeholder="Min" min="0" max="59" required>
                                        <input class="form-control col-3" type="number" name="seconds" placeholder="Sek" min="0" max="59" required>
                                    </div>
                                </div>
                            </div>
                            <div class="test-body">

                            </div>
                            <div>
                                <div class="col-12 px-0">
                                    <input type="checkbox" name="not-create-test">
                                    <label for="not-create-test">Nevytvářet nový test, pouze uložit otázky</label>
                                </div>
                                <div class="col-12 px-0">
                                    <input type="checkbox" name="practice">
                                    <label for="practice">Vytvářený test slouží jako procvičování</label>
                                </div>
                                <input type="submit" value="Uložit otázky" class="btn btn-success btn-lg btn-block mt-2">
                            </div>
                        </form>
                    </div>

                    <div class="test-div" id="assign-test">
                        <form action="/EngageTest" method="post">
                            <%
                                Teacher teacher = new Teacher(user, null);
                                DbManager manager = new DbManager();
                                teacher.loadClasses(manager.getConnection());
                                ArrayList<Test> tests = Test.retrieveTests(manager.getConnection());
                                for (Test test : tests) {
                                    test.loadQuestions(manager.getConnection());
                                    StringBuilder classesString = new StringBuilder();
                                    boolean appendAlert = true;
                                    for (Classs classs : teacher.getClassesWithBiggerGrade(test.getGrade())) {
                                        int numberOfStudentsWriting = 0;
                                        StringBuilder studentsString = new StringBuilder();
                                        for (Student student : classs.getStudents()) {
                                            student.loadEngagedTests(manager.getConnection());
                                            if (!student.hasWrittenTest(test.getId())) {
                                                numberOfStudentsWriting++;
                                                studentsString.append(student.engageStudentHTML(test.getId()));
                                            }
                                        }
                                        StringBuilder classString = classs.engageClassHTML(manager.getConnection(), studentsString);
                                        if (numberOfStudentsWriting != 0) {
                                            classesString.append(classString);
                                            appendAlert = false;
                                        }
                                    }
                                    if (appendAlert) {
                                        classesString.append("<strong class=\"pl-2\" style=\"color:red\">Žádnému uživateli není možné přidělit tento test</strong>");
                                    }
                                    out.print(test.testHTML(classesString));
                                }
                                //manager zavira connection az za chvili
                            %>
                            <div><label><input type="checkbox" name="volunteer"> Označit test jako dobrovolný pro všechny označené studenty</label></div>
                            <input type="submit" value="Přidělit testy" class="btn btn-success btn-lg btn-block mt-2">
                        </form>
                    </div>
                    <div class="test-div" id="run-test">
                        <form action="/RunTest">
                            <%
                                ArrayList<Student> students = EngagedTest.getStudentsNotStartedTests(manager.getConnection()); //already sorted by class
                                Classs lastClass = null;
                                if (students.size() != 0) {
                                    lastClass = students.get(0).getClasss();
                                }
                                StringBuilder studentsString = new StringBuilder();
                                for (Student student : students) {
                                    if (lastClass.getClassId() != student.getClasss().getClassId()) {
                                        out.print(lastClass.engageClassHTML(manager.getConnection(), studentsString));
                                        studentsString = new StringBuilder();
                                    }
                                    studentsString.append(student.runStudentHTML());
                                    lastClass = student.getClasss();
                                }
                                if (lastClass != null) {
                                    out.print(lastClass.engageClassHTML(manager.getConnection(), studentsString));
                                }
                                manager.closeConnection();
                            %>
                            <input type="submit" value="Odstartovat testy" class="btn btn-success btn-lg btn-block mt-2">
                        </form>
                    </div>
                    <div class="test-div" id="alter-test">
                        Alter Test
                    </div>

                </c:set>
            </c:when>
            <c:otherwise>
                <%
                    DbManager m = new DbManager();
                    Student student = new Student(user, m.getConnection());
                    student.loadEngagedTests(m.getConnection());
                    Set<Integer> terms = new HashSet();
                    for (EngagedTest test : student.getEngagedTests()) {
                        terms.add(test.getTerm());
                    }
                    String term = request.getParameter("term");
                    int highestTerm;
                    if (term == null) {
                        highestTerm = Integer.MIN_VALUE;
                        for (Integer i : terms) {
                            highestTerm = Math.max(highestTerm, i);
                        }
                    } else {
                        highestTerm = Integer.parseInt(term);
                    }
                    Iterator<EngagedTest> i = student.getEngagedTests().iterator();
                    while (i.hasNext()) {
                        if (i.next().getTerm() != highestTerm) {
                            i.remove();
                        }
                    }
                    for (EngagedTest test : student.getEngagedTests()) {
                        test.loadQuestions(m.getConnection());
                        test.loadAnswers(m.getConnection());
                        if (!test.isAlterable()) {
                            test.loadReachedPoints(m.getConnection());
                        }
                    }
                %>
                <c:set var="firstLink" value="Povinné testy"></c:set>
                <c:set var="secondLink" value="Procvičování"></c:set>
                <c:set var="firstSection">
                    <h2 class="my-3">${firstLink}</h2>
                    <form action="/Main/index.jsp">
                        <select name="term" style="width:100%" onchange="$(this).closest('form').submit()">
                            <%
                                for (Integer it : terms) {
                                    out.print("<option " + (it==highestTerm ? "selected" : "") + " value=\"" + it + "\">" + it + "</option>");
                                }
                            %>
                        </select>
                    </form>
                    <table class="table" id="obligatoryTests">
                        <thead>
                            <tr>
                                <th scope="col" class="test-name">Název testu</th>
                                <th class="d-none d-sm-table-cell test-result-deadline" scope="col">Odevzdat do</th>
                                <th class="test-result-points" scope="col">Výsledek</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                int maxPointsFromAllTests = 0;
                                int reachedPoints = 0;
                                for (EngagedTest test : student.getEngagedTests()) {
                                    if (!test.isPractice()) {
                                        out.print(test.resultTableRow());
                                        if (!test.isAlterable()) {
                                            reachedPoints += test.getReachedPoints();
                                            maxPointsFromAllTests += test.getMaxPointsWithStart();
                                        }
                                    }
                                }
                            %>
                        </tbody>
                        <tfoot>
                            <%
                                out.print(EngagedTest.resultTableSum(reachedPoints, maxPointsFromAllTests));
                            %>
                        </tfoot>
                    </table>
                </c:set>
                <c:set var="secondSection">
                    <h2 class="my-3">${secondLink}</h2>
                    <table class="table" id="obligatoryTests">
                        <thead>
                            <tr>
                                <th scope="col">Název testu</th>                               
                                <th class="d-none d-sm-table-cell test-result-deadline" scope="col">Odevzdat do</th>
                                <th class="test-result-points" scope="col">Výsledek</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                int maxPointsFromPracticeTests = 0;
                                int reachedPointsPracticeTests = 0;
                                for (EngagedTest test : student.getEngagedTests()) {
                                    if (test.isPractice()) {
                                        out.print(test.resultTableRow());
                                        if (!test.isAlterable()) {
                                            reachedPointsPracticeTests += test.getReachedPoints();
                                            maxPointsFromPracticeTests += test.getMaxPointsWithStart();
                                        }
                                    }
                                }
                            %>
                        </tbody>
                        <tfoot>
                            <%
                                out.print(EngagedTest.resultTableSum(reachedPointsPracticeTests, maxPointsFromPracticeTests));
                            %>
                        </tfoot>
                    </table>
                </c:set>
            </c:otherwise>
        </c:choose>
        <nav class="px-0 navbar navbar-expand-lg navbar-dark fixed-top">
            <div class="px-3 container-fluid">
                <a class="navbar-brand mt-1" href="#"><img src="images/logoTransparent.png" id="logoImage" class="mb-2"> ARACODE</a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNavDropdown">
                    <ul class="navbar-nav ml-auto">
                        <li class="nav-item" >
                            <a class="nav-link active" href="#first">${firstLink}</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" href="#second">${secondLink}</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" href="#footer">Kontakt</a>
                        </li>
                        <li class="nav-item">
                            <div class="dropdown active">
                                <button style="background-color: transparent; border-color: transparent; box-shadow: none; color: white; border:none; padding-top: 11px" 
                                        class="btn dropdown-toggle pl-0 pl-lg-2"type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <%                                        if (user != null) {
                                            out.print(user.getFirstName() + " " + user.getLastName());
                                        }
                                    %>
                                </button>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                                    <a class="dropdown-item pl-1 pl-lg-4" href="/Login/changePassword.jsp">Změnit heslo</a>
                                    <a class="dropdown-item pl-1 pl-lg-4" href="/LogoutController">Odhlásit se</a>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <section id="first">
            <div class="container-fluid">
                ${firstSection}
            </div>
        </section>
        <section id="second">
            <div class="px-3 container-fluid">
                ${secondSection}
            </div>
        </section>
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