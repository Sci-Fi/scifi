<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <title>Controlador Scifi - MRTG</title>
        <link href="css/style.css" rel="stylesheet" type="text/css" />
        <link href="css/jquery.dataTables.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="javascript/jquery-1.9.1.min.js"></script>
        <script type="text/javascript" src="javascript/script.js"></script>
        <script type="text/javascript" src="javascript/jquery.dataTables.min.js"></script>
    </head>
    <body>

        <?php
                //$ds = ldap_connect('www.uff.br', 80);
        ?>

        <div id="tudo">

            <div id="topo">
                <a href="." id="logo" title="MRTG"></a>


                <div id="figuraTopo"></div>

            </div>

            <div id="barraTopo"></div>

            <div id="coluna_esq">

                <div id="menuTopo">MENU</div>

                <div class="barraMenu"></div>

                <div id="menu">
                    <ul>
                        <li class="menu_classes">
                            <a id="classe" href="#" title="Gr&aacute;ficos de Compara&ccedil;&atilde;o"></a>
                            <ul>
                                <li id="network">
                                    <a href="index.php?page=classes&classe=network&periodo=day" title="Tr&aacute;fego na Rede"></a>
                                </li>
                                <li id="user">
                                    <a href="index.php?page=classes&classe=user&periodo=day" title="N&uacute;mero de Usu&aacute;rios"></a>
                                </li>
                                <li id="uptime">
                                   <a href="index.php?page=classes&classe=upt&periodo=day" title="UPTIME"></a>
                                </li>
                                <li id="memory">
                                    <a href="index.php?page=classes&classe=memory&periodo=day" title="Uso de Mem&oacute;ria"></a>
                                </li>
                                <li id="cpu">
                                    <a href="index.php?page=classes&classe=cpu&periodo=day" title="Uso de CPU"></a>
                                </li>
                            </ul>
                        </li>
                        <li id="devices">
                            <a href="index.php?page=devices" title="Dispositivos"></a>
                        </li>
                        <li class="menu_server">
                                <a id="server" href="#" title="Informa&ccedil;&otilde;es dos Servidores"></a>
                                <ul>
                                        <li id="disk">
                                         <a href="index.php?page=classes&classe=disk&periodo=day" title="Uso de Discos"></a>
                                        </li>
                                        <li id="leases">
                                         <a href="index.php?page=classes&classe=leases&periodo=day" title="Numero de Leases"></a>
                                        </li>
                                </ul>
                    </ul>
                </div>
            </div>

            <div id="coluna_dir">
                <div id="titulo">O diret&oacute;rio listado &eacute; <?php echo "/var/www/mrtg/"; ?> <!--- Oi, <?php //echo $_SERVER['PHP_AUTH_USER']?> e sua senha é <?php //echo $_SERVER['PHP_AUTH_PW']?>--></div>
                <div class="barraConteudo"></div>
                <div id="conteudo">
                    <div id="pages">
                         <?php
                            $page = $_GET["page"];
                            $ap = $_GET["ap"];
                            $interface = $_GET["interface"];
                            $classe = $_GET["classe"];
                            $periodo = $_GET["periodo"];
                            $conjunto = $_GET["conjunto"];



                            if($page != "") {
                                if($ap == "" && $interface == "" && $classe == "") {
                                    include_once $page."/index.php";

                                } else if ($ap != "" && $interface == "" && $classe == "") {
                                    include_once $ap."/index.php";

                                } else if ($ap != "" && $interface != "" && $classe == "") {
                                    include_once $ap."/".$interface;

                                } else if ($ap == "" && $interface == "" && $classe != "") {
                                    include_once $page."/".$classe.".php";

                                } else {
                                    echo "Pagina não encontrada !";
                                }
                            } else {
                                echo ("<h3>Monitoramento de Dispositivos da rede WIFI-UFF</h3>");
                            }
                        ?>
                    </div>
                </div>
            </div>

            <div id="rodape">
                <ul>
                    <li class="uff"></li>
                    <li class="midiacom"><a href="http://www.midiacom.uff.br/" title="Laboratório Mídiacom"></a></li>
                    <li class="computacao"><a href="http://www.ic.uff.br/" title="Instituto de Computação UFF"></a></li>
                    <li class="engenharia"><a href="http://www.engenharia.uff.br/" title="Escola de Engenharia UFF"></a></li>
                    <li class="rnp"><a href="http://www.rnp.br/" title="Rede Nacional de Pesquisa"></a></li>
                </ul>
            </div>

        </div>
    </body>
</html>
