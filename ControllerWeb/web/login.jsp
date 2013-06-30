<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - Login</title>
        <link href="../css/estilo.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div id="tudo">

            <div id="topo">

                <div id="logo"><ul><li><a href="admin.jsf" title="Voltar à página inicial"></a></li></ul></div>

                <div id="figuraTopo"></div>

            </div>

            <div id="barraTopo"></div>

            <div id="coluna_central">
                <div id="titulo"></div>
                <div class="barraConteudo"></div>
                <div id="conteudo">
                    <div id="login">
                        <form action="j_security_check" method="post">
                            <table>
                                <tr>
                                    <th colspan="2">Login</th>
                                </tr>
                                <tr>
                                    <td>
                                        Nome de usuário:
                                    </td>
                                    <td class="inputTexto">
                                        <input type="text" name="j_username" class="texto" />
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Senha:
                                    </td>
                                    <td class="inputTexto">
                                        <input type="password" name="j_password" class="texto" />
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" class="linhabotoes">
                                        <input type="submit" value="Entrar" class="botao" />
                                        <input type="reset" value="Limpar" class="botao" />
                                    </td>
                                </tr>
                            </table>
                        </form>
                    </div>
                </div>
            </div>        

            <div id="rodape">
                <ul>
                    <li class="uff"><a href="http://www.uff.br/" title="Universidade Federal Fluminense"></a></li>
                    <li class="midiacom"><a href="http://www.midiacom.uff.br/" title="Laboratório Mídiacom"></a></li>
                    <li class="computacao"><a href="http://www.ic.uff.br/" title="Instituto de Computação UFF"></a></li>
                    <li class="engenharia"><a href="http://www.engenharia.uff.br/" title="Escola de Engenharia UFF"></a></li>
                    <li class="rnp"><a href="http://www.rnp.br/" title="Rede Nacional de Pesquisa"></a></li>
                </ul>
            </div>           

        </div>
    </body>
</html>