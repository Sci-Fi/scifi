<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - Executar Comandos</title>
        <script type="text/javascript" src="../javascript/jquery-1.8.3.min.js"></script>
        <link rel="stylesheet" href="../css/thickbox.css" type="text/css" media="screen" />
        <script language="javascript" type="text/javascript" src="../javascript/thickbox.js"></script>
        <script type="text/javascript" src="../javascript/preloadImages.js"></script>
        <script type="text/javascript" src="../javascript/preloadImages_Commander.js"></script>
        <script type="text/javascript" charset="UTF-8" src="../javascript/utils.js"></script>
        <script type="text/javascript" src="../javascript/commander.js"></script>
        <script type="text/javascript" charset="UTF-8" src="../javascript/time_aps.js"></script>
        <script type="text/javascript">    
            ShowExecutionStatus();
        </script>
        <link href="../css/estilo.css" rel="stylesheet" type="text/css" />
    </head>
    <body id="commander">
        <div id="tudo">

            <div id="topo">

                <div id="logo"><ul><li><a href="admin.jsf" title="Voltar à página inicial"></a></li></ul></div>

                <div id="figuraTopo"></div>

            </div>

            <div id="barraTopo"></div>

            <div id="coluna_esq">

                <div id="menuTopo">MENU</div>

                <div class="barraMenu"></div>

                <div id="menu">
                    <ul>
                        <li class="APs"><a href="#" title="Pontos de Acesso"></a>
                            <ul>
                                <li class="adicionarAP"><a href="new_ap.jsf" title="Adicionar Ponto de Acesso"></a></li>
                                <li class="editarAPs"><a href="edit_ap_info.jsf" title="Editar Pontos de Acesso"></a></li>
                                <li class="visualizarAPs"><a href="ap_info.jsf" title="Visualizar Pontos de Acesso"></a></li>
                            </ul>
                        </li>
                        <li class="regioes"><a href="#" title="Regiões de Controle"></a>
                            <ul>
                                <li class="adicionarRegiao"><a href="new_region.jsf" title="Adicionar Região de Controle"></a></li>
                                <li class="excluirRegiao"><a href="remove_region.jsf" title="Excluir Região de Controle"></a></li>
                            </ul>
                        </li>
                        <li class="comandos"><a href="commander.jsf" title="Executar Comandos do Controlador"></a></li>
                        <li class="configurar"><a href="edit_parameters.jsf" title="Editar Configurações do Controlador"></a></li>
                        <li class="mrtg"><a href="editMap.jsp?type=10" target="_blank" title="Monitoramento"></a></li>
                    </ul>
                </div>
            </div>

            <div id="coluna_dir">
                <div id="titulo">Executar Comandos do Controlador<label id="info_unreachable"><label></label><a href="#" title="Controlador Scifi - Pontos de acesso incomunicantes" class="thickbox"></a><img src="../figuras/wait.gif"/></label><a href="logout.jsf" id="logout">Logout</a> </div>
                <div class="barraConteudo"></div>
                <div id="conteudo">

                    <f:view>
                        <h:form styleClass="formComandos">
                            
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceRestart}" onclick="javascript: trocaCursor('progress',this);" title="Reiniciar Controlador" styleClass="reiniciarControlador"></h:commandButton></td>
                                            <td><h:outputText value="Reiniciar Controlador" /></td>
                                        </tr>
                                    </table>
                                </div>

                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceTimerRestart}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Reinicio dos Temporizadores" styleClass="reiniciarTemporizadores"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Reinicio dos Temporizadores" /></td>
                                        </tr>
                                    </table>
                                </div>

                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceChannelSelection}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Seleção de Canal" styleClass="selecaoCanal"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Seleção de Canal" /></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forcePowerControl}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Controle de Potência" styleClass="controlePotencia"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Controle de Potência" /></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceScan}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Escaneamento do Ambiente" styleClass="scan"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Escaneamento do Ambiente" /></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceSTAInfoCollection}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Coleta de Dados dos Usuários" styleClass="coletaUsuarios"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Coleta de Dados dos Usuários" /></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.rebootAll}" onclick="javascript: trocaCursor('progress',this);" title="Reiniciar todos os Pontos de Acesso" styleClass="reiniciarAPs"></h:commandButton></td>
                                            <td><h:outputText value="Reiniciar todos os Pontos de Acesso" /></td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="divComando">
                                    <table class="tableComando">
                                        <tr>
                                            <td><h:commandButton action="#{JControllerCommanderBean.forceConfigCheck}" onclick="javascript: trocaCursor('progress',this);" title="Forçar Análise de Configurações dos Pontos de Acesso" styleClass="analiseAPs"></h:commandButton></td>
                                            <td><h:outputText value="Forçar Análise de Configurações dos Pontos de Acesso" /></td>
                                        </tr>
                                    </table>
                                </div>
                            
                        </h:form>
                    </f:view>

                </div>
            </div>        
            <div class="clr"></div>

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
