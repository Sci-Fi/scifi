<%-- 
    Document   : logout
    Created on : 16/07/2012, 16:32:11
    Author     : carlosmaciel
--%>

<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<%
    //Fecha a sessão e redireciona para a página inicial, como a sessão está fechada,
    //  ele reenvia para a pagina de login, quando o usuario loga, ele envia para a pagina inicial

    session.invalidate();
    response.sendRedirect("admin.jsf");
%>
