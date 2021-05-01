<%-- 
    Document   : changePassword
    Created on : 28.1.2020, 15:56:07
    Author     : Vavra
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>ARACODE - Zapomenuté heslo</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!--<script src="https://apis.google.com/js/platform.js" async defer></script> obcas se neinicializuje-->
        <script src="https://apis.google.com/js/api:client.js"></script>
        <meta name="google-signin-client_id" content="233579605126-m7ej8hl5a5srh0ncp7pik1va57aemgrv.apps.googleusercontent.com">
        <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" type="text/css" href="CSS/util.css">
        <link rel="stylesheet" type="text/css" href="CSS/main.css">
        <link rel="icon" href="../Main/images/logoTransparent.png">
    </head>
    <body>
        <div class="limiter">
            <div class="container-login100">
                <div class="wrap-login100 p-l-85 p-r-85 p-t-55 p-b-55">
                    <c:choose>
                        <c:when test="${not empty param.token}">
                            <form action="/NewPasswordController" method="post" class="login100-form validate-form flex-sb flex-w">
                                <span class="login100-form-title p-b-32">
                                    Změna hesla
                                </span>
                                <input type="hidden" name="token" value="${param.token}" />
                            </c:when>
                            <c:otherwise>
                                <form action="/ChangePasswordController" method="post" class="login100-form validate-form flex-sb flex-w">
                                    <span class="login100-form-title p-b-32">
                                        Změna hesla
                                    </span>
                                    <span class="txt1 p-b-11">
                                        Staré heslo
                                    </span>
                                    <div class="wrap-input100 validate-input m-b-12" data-validate="Nezadali jste heslo">
                                        <span class="btn-show-pass">
                                            <i class="fa fa-eye"></i>
                                        </span>
                                        <input class="input100" type="password" name="oldPass" >
                                        <span class="focus-input100"></span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <span class="txt1 p-b-11">
                                Nové heslo
                            </span>
                            <div class="wrap-input100 validate-input m-b-12" data-validate="Nezadali jste heslo">
                                <span class="btn-show-pass">
                                    <i class="fa fa-eye"></i>
                                </span>
                                <input class="input100" type="password" name="newPass" >
                                <span class="focus-input100"></span>
                            </div>
                            <div class="flex-sb-m w-full container-login100-form-btn m-t-12">
                                <button class="w-full login100-form-btn">
                                    Změnit heslo
                                </button>
                            </div>
                        </form>
                </div>
            </div>
        </div>


        <div id="dropDownSelect1"></div>

        <script src="../lib/jquery/jquery-3.2.1.min.js"></script>
        <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="JS/main.js"></script>
    </body>
</html>