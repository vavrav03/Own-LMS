<%-- 
    Document   : login
    Created on : 28.1.2020, 15:39:50
    Author     : Vavra, Colorlib (odcitován v dokumentaci)
--%>

<%@page import="login.LoginController"%>
<%@page import="database.DbManager"%>
<%@page import="database.User"%>
<%@page import="login.Hasher"%>
<%@page import="org.apache.catalina.tribes.util.Arrays"%>
<%@page import="database.RememberMeToken"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>ARACODE - Přihlášení</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!--<script src="https://apis.google.com/js/platform.js" async defer></script> obcas se neinicializuje-->
        <!--<script src="https://apis.google.com/js/api:client.js"></script>-->
        <meta name="google-signin-client_id" content="233579605126-m7ej8hl5a5srh0ncp7pik1va57aemgrv.apps.googleusercontent.com">
        <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" type="text/css" href="CSS/util.css">
        <link rel="stylesheet" type="text/css" href="CSS/main.css">
        <link rel="icon" href="../Main/images/logoTransparent.png">
    </head>
    <body>
        <%
            if (session.getAttribute("user") != null || LoginController.isLogged(request, response, session)) {
                response.sendRedirect("../Main/index.jsp");
            }

        %>
        <div class="limiter">
            <div class="container-login100">
                <div class="wrap-login100 p-l-85 p-r-85 p-t-55 p-b-55">
                    <form action="/LoginController" method="post" class="login100-form validate-form flex-sb flex-w">
                        <span class="login100-form-title p-b-32">
                            Přihlášení
                        </span>
                        <span class="txt1 p-b-11">
                            E-mail
                        </span>
                        <div class="wrap-input100 validate-input m-b-36" data-validate="Nezadali jste e-mail">
                            <input class="input100" type="text" name="email" >
                            <span class="focus-input100"></span>
                        </div>
                        <span class="txt1 p-b-11">
                            Heslo
                        </span>
                        <div class="wrap-input100 validate-input m-b-12" data-validate="Nezadali jste heslo">
                            <span class="btn-show-pass">
                                <i class="fa fa-eye"></i>
                            </span>
                            <input class="input100" type="password" name="pass" >
                            <span class="focus-input100"></span>
                        </div>
                        <div class="flex-sb-m w-full p-b-24">
                            <div class="contact100-form-checkbox">
                                <input class="input-checkbox100" id="ckb1" type="checkbox" name="remember-me"> 
                                <label class="label-checkbox100" for="ckb1">
                                    Zapamatovat si mě
                                </label>
                            </div>
                            <div>
                                <a href="forgotPassword.html" class="txt3">
                                    Zapomenuté heslo
                                </a>
                            </div>
                        </div>
                        <c:set var="showMessage" value="${true}" />  
                        <c:choose>
                            <c:when test="${param.option == 'LoginFail'}">
                                <c:set var="message" value="Zadali jste špatné přihlašovací údaje" />
                                <c:set var="messageClass" value="alert-danger" />  
                            </c:when>
                            <c:when test="${param.option == 'EmailSent'}">
                                <c:set var="message" value="Klikněte na odkaz z e-mailu, změňte heslo a poté se zde přihlašte" />
                                <c:set var="messageClass" value="alert-success" />  
                            </c:when>
                            <c:when test="${param.option == 'EmailNotSent'}">
                                <c:set var="message" value="E-mail se na zadanou adresu nepodařilo odeslat" />
                                <c:set var="messageClass" value="alert-danger" />  
                            </c:when>
                            <c:when test="${param.option == 'resetNotSuccessfull'}">
                                <c:set var="message" value="Změna hesla se nezdařila" />
                                <c:set var="messageClass" value="alert-danger" />  
                            </c:when>
                            <c:when test="${param.option == 'resetSuccessfull'}">
                                <c:set var="message" value="Vaše heslo bylo změněno" />
                                <c:set var="messageClass" value="alert-success" />   
                            </c:when>
                            <c:otherwise>
                                <c:set var="showMessage" value="${false}" />   
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${showMessage}">
                            <div class="alert ${messageClass} alert-dismissible fade show m-0 w-full" role="alert">
                                <strong>${message}</strong>
                                <button class="close p-0" type="button" data-dismiss="alert" aria-label="Close">
                                    <span class="mr-2" aria-hidden="true">&times;</span>
                                </button>
                            </div>
                        </c:if>
                        <div class="flex-sb-m w-full container-login100-form-btn p-t-24">
                            <button class="login100-form-btn">
                                Přihlásit se
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="../lib/jquery/jquery-3.2.1.min.js"></script>
        <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="JS/main.js"></script>
    </body>
</html>