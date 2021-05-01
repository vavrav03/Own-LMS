<%-- 
    Document   : tests
    Created on : 31.1.2020, 10:45:47
    Author     : mecme
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="CSS/sheets.css">
        <link rel="icon" href="../Main/images/logoTransparent.png">
        <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css">
    </head>
    <body>
        <table class="table">
            <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">First</th>
                    <th scope="col">Last</th>
                    <th scope="col">Handle</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <th scope="row">1</th>
                    <td>Mark</td>
                    <td>Otto</td>
                    <td>@mdo</td>
                </tr>
                <tr>
                    <th scope="row">2</th>
                    <td>Jacob</td>
                    <td>Thornton</td>
                    <td>@fat</td>
                </tr>
                <tr>
                    <th scope="row">3</th>
                    <td colspan="2">Larry the Bird</td>
                    <td>@twitter</td>
                </tr>
            </tbody>
        </table>
        <label class="mdb-main-label">Blue select</label>
        <script src="../lib/jquery/jquery-3.2.1.min.js"></script>
        <script src="../lib/bootstrap/js/popper.js"></script>
        <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
        <script src="JS/main.js"></script>
    </body>
</html>
