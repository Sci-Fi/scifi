<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - Bem Vindo!</title>
        <link rel="stylesheet" href="../css/map.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="../css/thickbox.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="../css/jquery.treeview.css" />
        <!--<link rel="stylesheet" href="../css/jquery-ui-1.8.23.custom.css" />-->
        <link rel="stylesheet" href="../css/estilo.css" type="text/css" />
        <script type="text/javascript" src="../javascript/jquery-1.8.3.min.js"></script>
        <script type="text/javascript" src="../javascript/preloadImages.js"></script>
        <script language="javascript" type="text/javascript" src="../javascript/thickbox.js"></script>
        <script src="https://maps-api-ssl.google.com/maps/api/js?v=3&sensor=false" type="text/javascript"></script>
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
        <script type="text/javascript" src="../javascript/gmap3.js"></script>
        <script type="text/javascript" src="../javascript/gmap3-menu.js"></script>
        <script src="../javascript/jquery.treeview.js" type="text/javascript"></script>
        <script src="../javascript/utils.js" type="text/javascript"></script>
        <script src="../javascript/newAP.js" type="text/javascript"></script>
        <script src="../javascript/editAPMap.js" type="text/javascript"></script>
        <script type="text/javascript" charset="UTF-8" src="../javascript/map.js"></script>
        <script type="text/javascript">
            ShowInsertionStatus();
            ShowUpdateStatus();
        </script>
    </head>

    <body>
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
                <div id="titulo">Bem Vindo! <label id="info_unreachable"><label></label><a href="#" title="Controlador Scifi - Pontos de acesso incomunicantes" class="thickbox"></a><img src="../figuras/wait.gif"/></label> <a href="logout.jsf" id="logout">Logout</a> </div>
                <div class="barraConteudo"></div>
                <div id="conteudo">
                    <p>Bem vindo à sessão administrativa do <b>S</b>istema de <b>C</b>ontrole <b>I</b>nteligente para Redes Sem <b>Fi</b>o (SciFi).</p>
                    <p>Utilize os links contidos na barra esquerda para realizar ações.</p>
                    <p>Antes de adicionar Pontos de Acesso, adicione uma Região de Controle. Todo Ponto de Acesso deverá estar contido em uma Região de Controle</p>
                    <p>O controlador funciona automaticamente, entretando, se desejar forçar a execução de comandos utilize o link "Executar Comandos do Controlador".</p>
                    <p>Altere parâmetros do controlador através do link "Editar Configurações do Controlador".</p>

                </div>

                <div class="clr"></div>
                <input type="hidden" id="dataMap"/>

                <div id="optionsMap">
                    <div id="filterSearch_Map">Filtro: <input type="text" id="searchAP_Map" value="Insira o MAC ou IP aqui" default="default"/> <input type="button" id="searchMACAP_Map" value="Ir"/></div>
                    <label id="infoSearchAP_Map"></label>
                </div>
                <div id="googleMap"></div>
                <div id="legenda" >
                    <label>Legenda:</label>
                    <ul>
                        <li title="Ponto de Acesso a ser cadastrado.">
                            <img width="14" height="14" src="../figuras/mapa/white-wifi.png">
                                Novo
                        </li>
                        <li title="Ponto de acesso está habilitado e sendo gerenciado pelo Controlador.">
                            <img width="14" height="14" src="../figuras/mapa/green-wifi.png">
                                Habilitado
                        </li>
                        <li title="Ponto de acesso NÃO está habilitado, ou seja, o Controlador não gerenciará o Ponto de Acesso (AP). Todavia, o AP permanece funcional.">
                            <img width="14" height="14" src="../figuras/mapa/red-wifi.png">
                                Desabilitado
                        </li>
                        <li title="Ponto de acesso não está comunicando com o sistema via rede cabeada.">
                            <img width="14" height="14" src="../figuras/mapa/orange-wifi.png">
                                Incomunicante
                        </li>
                    </ul>
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

