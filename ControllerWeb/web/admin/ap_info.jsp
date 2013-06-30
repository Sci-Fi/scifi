<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - Visualizar Pontos de Acesso</title>
        <script type="text/javascript" src="../javascript/preloadImages.js"></script>
        <script type="text/javascript" src="../javascript/preloadImages_apInfo.js"></script>
        <script type="text/javascript" src="../javascript/utils.js"></script>
        <script type="text/javascript" src="../javascript/apInfo.js"></script>
        <script type="text/javascript" src="../javascript/commander.js"></script>

        <script type="text/javascript">    
            ShowExecutionStatus();
        </script>
        <link href="../css/estilo.css" rel="stylesheet" type="text/css" />
    </head>
    <body id="apInfo">
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
                <div id="titulo">Visualizar Pontos de Acesso</div>
                <div class="barraConteudo"></div>
                <div id="conteudo">

                    <f:view>
                        <h:form>


                            <h:dataTable id="dt1" var="item" value="#{JAPListBean.listAP}" rowClasses="#{JAPListBean.rowColorEnabled}">

                                <f:facet name="caption">
                                    <h:outputText value="Tabela de uso dos pontos de acesso" />
                                </f:facet>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value=""></h:outputText>
                                    </f:facet>
                                    <h:outputText value=" #{JRowCounterBean.row} "></h:outputText>
                                </h:column> 


                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="MAC"></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.MAC}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="IP" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.IP}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Localização" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.location}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Região" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.regionName}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Canal" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.channel}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Lista de Potências" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.listTxPower}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Potência Atual" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.curTxPower}"></h:outputText>
                                </h:column>

                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Número de Usuários" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.numberOfUsers}"></h:outputText>
                                </h:column>

                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Status" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.loadStatusText}"></h:outputText>
                                </h:column>

                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Limite de Carga Baixa" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.underloadThreshold}"></h:outputText>
                                </h:column>

                                <h:column>
                                    <f:facet name="header">
                                        <h:outputText value="Limite de Sobrecarga" ></h:outputText>
                                    </f:facet>
                                    <h:outputText value="#{item.overloadThreshold}"></h:outputText>
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Informação de Scan" />
                                    </f:facet>
                                    <div class="visualizarScan">
                                        <ul>
                                            <li>
                                                <h:outputLink value="scan_info.jsf?MAC=#{item.MAC}" title="Visualizar Informações de Scan" />
                                            </li>
                                        </ul>
                                    </div>      
                                </h:column>

                                <h:column >
                                    <f:facet name="header" >
                                        <h:outputText value="Ações" />
                                    </f:facet>

                                    <h:commandButton actionListener="#{JAPListBean.selectMAC}" action="#{JAPListBean.removeAP}" onclick="javascript: return ConfirmRemoval();" title="Remover" styleClass="remover">
                                        <f:attribute name="selectedMAC" value="#{item.MAC}"/>
                                    </h:commandButton>

                                    <h:commandButton actionListener="#{JControllerCommanderBean.selectMAC}" action="#{JControllerCommanderBean.rebootAP}" onclick="javascript: return ConfirmReboot();" title="Reiniciar" styleClass="reiniciar">
                                        <f:attribute name="selectedMAC" value="#{item.MAC}"/>
                                    </h:commandButton>

                                    <h:commandButton actionListener="#{JAPListBean.selectMAC}" action="#{JAPListBean.enableAP}" rendered="#{!item.enabled}" styleClass="habilitar" title="Habilitar">
                                        <f:attribute name="selectedMAC" value="#{item.MAC}"/>
                                    </h:commandButton>

                                    <h:commandButton actionListener="#{JAPListBean.selectMAC}" action="#{JAPListBean.enableAP}"  rendered="#{item.enabled}" styleClass="desabilitar" title="Desabilitar">
                                        <f:attribute name="selectedMAC" value="#{item.MAC}"/>
                                    </h:commandButton>
                                </h:column>                    
                            </h:dataTable>
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
