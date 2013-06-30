<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - Adicionar Região de Controle</title>
        <script type="text/javascript" src="../javascript/preloadImages.js"></script>
        <script type="text/javascript" src="../javascript/utils.js"></script>
        <script type="text/javascript" src="../javascript/newRegion.js" charset="utf-8"></script>
        <script type="text/javascript">    
            ShowInsertionStatus();
        </script>
        <link href="../css/estilo.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div id="tudo">

            <div id="topo">

                <div id="logo"><ul><li><a href="admin.html" title="Voltar à página inicial"></a></li></ul></div>

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
                    </ul>
                </div>
            </div>

            <div id="coluna_dir">
                <div id="titulo">Adicionar Região de Controle</div>
                <div class="barraConteudo"></div>
                <div id="conteudo">
                    <div id="tableCenter">
                        <f:view>
                            <h:form id="newRegionForm" >
                                <table>
                                    <tr>
                                        <td colspan="2" class="tdERRMsg"></td>
                                    </tr>
                                    <tr>
                                        <td class="tdFormat"><h:outputText value="Nome da Região:" /></td>
                                        <td class="tdInput"><h:inputText id="RegionName" value="#{JRegionBean.regionName}" validator="#{JRegionValidator.checkRegionName}"/></td>
                                    </tr>
                                    <tr>
                                        <td colspan="2" class="tdERRMsg"><h:message for="RegionName" errorClass="errorMessage" infoClass="infoMessage" /></td>
                                    </tr>
                                </table>
                                <h:panelGrid columns="1" styleClass="tableFinal">
                                    <h:commandButton action="#{JRegionBean.addRegion}" value="Adicionar" styleClass="submit"></h:commandButton>
                                </h:panelGrid>
                            </h:form>
                        </f:view>
                    </div>
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
