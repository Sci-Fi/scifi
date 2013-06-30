<%-- 
    Document   : splash
    Created on : Apr 8, 2011, 1:54:33 PM
    Author     : Felipe Rolim
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>


<html>
    <head><title>Quantidade de usuários</title>        
        <link rel="stylesheet" type="text/css" href="../ControllerWeb/css/estilo.css" />
    </head>
    <body class="bodySplash">

        <center>
            
            <f:view>
               <h:form id="form1">                 

                   <h:panelGrid columns="1" style="text-align:center" cellspacing="15" rendered="#{not empty JAPListBean.userLocation}">  
                       <h:outputText value="Você está conectado no ponto de acesso: " style="locationtext"/>   
                       <h:outputText value="#{JAPListBean.userLocation}" style="locationtext"/>   
                    </h:panelGrid>  
                        
                   <h:dataTable id="dt1" var="item" value="#{JAPListBean.listAP}" rowClasses="#{JAPListBean.rowColor}" footerClass="rodape" styleClass="tabela" columnClasses="td,td,td" captionClass="caption">
                        <f:facet name="caption">
                            <h:outputText value="Tabela de uso dos pontos de acesso" />
                        </f:facet>

                        <h:column >
                            <f:facet name="header" >
                                <h:outputText value="Localização" />
                            </f:facet>
                            <h:outputText value="#{item.location}"></h:outputText>
                        </h:column>

                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Número de usuários"/>
                            </f:facet>
                            <h:outputText value="#{item.numberOfUsers}"></h:outputText>
                        </h:column>

                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Status"/>
                            </f:facet>
                            <h:outputText value="#{item.loadStatusText}"></h:outputText>
                        </h:column>

                        <f:facet name="footer">
                            <h:outputText value="Total de usuários conectados à rede: #{JAPListBean.numberOfUsers}" />
                        </f:facet>
                    </h:dataTable>
                </h:form>
            </f:view>
        </center>
    </body>
</html>
