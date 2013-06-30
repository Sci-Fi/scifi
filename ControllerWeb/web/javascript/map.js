var ENABLED_AP = 1;
var dados_ap = [];
var regions = [];
var addinfowindow;
var infowindow_active = null;
var marker_infowindow_active = null;
var URL_MRTG = "#";

$(document).ready(function(){

    var latitude_default = 0;
    var longitude_default = 0;
    var zoom_default = 0;
    var incrementa_id_marcador_novo = 0;
    
    $.ajax({
        cache: false,
        type: "GET",
        url: "editMap.jsp",
        data: "type=11",
        dataType: "text",
        async: false,
        success: function (data) {     
            URL_MRTG = data;
        }
    }).error(function () {
        URL_MRTG = "#";
    });    
    
    //editMap.jsp?type=11&ap_mrtg=' + AP_MRTG + '

    $("#searchAP_Map").click(function () {
        if ($("#searchAP_Map").attr("default") == "default") {
            $("#searchAP_Map").val("");
            $("#searchAP_Map").css("font-style","normal");
            $("#searchAP_Map").css("color","#000");
            $("#searchAP_Map").attr("default","none");
        }
    });  

    $("#searchAP_Map").keydown(function (evt) {
        if (evt.which == 13) {
            $("#searchMACAP_Map").focus();
            $("#searchMACAP_Map").trigger("click");
        }
    });

    $("#searchAP_Map").focusout(function () {
        if ($("#searchAP_Map").val() == "") {            
            $("#searchAP_Map").attr("default","default");
            $("#searchAP_Map").css("font-style","italic");
            $("#searchAP_Map").css("color","#C0C0C0");
            $("#searchAP_Map").val("Insira o MAC ou IP aqui");
        } else {
            $("#searchAP_Map").attr("default","none");
        }
    });

    //PREENCHENDO OS MARCADORES
    $.ajax({
        cache: false,
        type: "GET",
        url: "editMap.jsp",
        data: "type=5",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data, function(key, val) {

                switch(val.enabled) {
                    case 0: //DESABILITADO
                        dados_ap.push({
                            latLng: [val.latitude, val.longitude],
                            data: data[key],
                            options:{
                                icon: new google.maps.MarkerImage('../figuras/mapa/red-wifi.png')
                            },
                            id: val.MAC
                        });
                        break;
                    case 1:  //HABILITADO
                        if (val.reachable == 1) {
                            dados_ap.push({
                                latLng: [val.latitude, val.longitude],
                                data: data[key],
                                options:{
                                    icon: new google.maps.MarkerImage('../figuras/mapa/green-wifi.png')
                                },
                                id: val.MAC
                            });
                        } else { //HABILITADO PORÉM INCOMUNICAVEL
                            dados_ap.push({
                                latLng: [val.latitude, val.longitude],
                                data: data[key],
                                options:{
                                    icon: new google.maps.MarkerImage('../figuras/mapa/orange-wifi.png')
                                },
                                id: val.MAC
                            });
                        }
                        
                        break;
                }
            });
        }
    }).error(function () {
        return false;
    });

    //CENTRALIZANDO O MAPA COM DADOS DEFAULT
    $.ajax({
        cache: false,
        type: "GET",
        url: "editMap.jsp",
        data: "type=6",
        dataType: "json",
        async: false,
        success: function (data) {     
            latitude_default = data.latitude
            longitude_default = data.longitude
            zoom_default = data.zoom
        }
    }).error(function () {
        return false;
    });
    
    var $map = $('#googleMap'),
    menu = new Gmap3Menu($map),
    current,  
    center = [latitude_default, longitude_default];

    /*
     * Busca AP (no mapa) por MAC ou IP. E mostra as informa��es dele pelo bal�o (infowindow).
     */
    $("#searchMACAP_Map").click(function () {
        $.ajax({
            cache: false,
            type: "GET",
            url: "editMap.jsp",
            data: "type=2&MACorIP=" + $("#searchAP_Map").val().trim(),
            dataType: "json",
            async: false,
            success: function (data) {
                if(data.length == 1) {
                    $map.gmap3('get').setCenter(new google.maps.LatLng(data[0].latitude, data[0].longitude));
                    $map.gmap3('get').setZoom(20);

                    var context = [];
                    var MAC = "";

                    $.each(dados_ap, function(key, val) {
                        if((val.data.MAC == $("#searchAP_Map").val().trim()) || (val.data.IP == $("#searchAP_Map").val().trim())) {
                            context = val.data;
                            MAC = val.data.MAC;
                        }
                    });

                    var marker = $map.gmap3({
                        get: {
                            id: MAC
                        }
                    });

                    infowindow_active = addinfowindow(marker, context);
                    marker_infowindow_active = marker;
                    
                    $("#infoSearchAP_Map").html("");
                } else {
                    
                    $("#infoSearchAP_Map").html("A pesquisa encontrou nenhum resultado !");
                    
                    setTimeout(function(){                        
                        $("#infoSearchAP_Map").html("");
                    }, 3000);
                }
            }
        }).error(function () {
            return false;
        });
    });

    /*
     * Adciona um marcador no mapa
     */
    function addMarker(id){
        // add marker and store it        
        $map.gmap3({
            marker:{
                latLng: current.latLng,
                options:{
                    draggable:true,
                    icon: new google.maps.MarkerImage('../figuras/mapa/white-wifi.png'),
                    animation: google.maps.Animation.DROP
                },
                id: id,
                events: {
                    dragend: function(marker, event, context){
                        $(".add_marker_latitude").val(marker.getPosition().lat());
                        $(".add_marker_longitude").val(marker.getPosition().lng());
                        $(".add_marker_listaDados li").html("Posição atual: (" + roundNumber(marker.getPosition().lat(), 6) + ", " + roundNumber(marker.getPosition().lng(), 6) + ")");
                        $(".listCommandAP_Map").resize();
                    },
                    click: function(marker, event, data){
                        var map = $(this).gmap3('get'),
                        infowindow = $(this).gmap3({
                            get:{
                                name: "infowindow"
                            }
                        });
                        var ID = data.id

                        var infowindowtext = '<div class="listCommandAP_Map"><h4>Novo Ponto de Acesso</h4>\n\
                                            <form method="POST" id="newAPMap" name="newAPMap" action="new_APMap.jsf">\n\
                                                <input type="hidden" class="add_marker_latitude" name="latitude" value="' + marker.getPosition().lat() + '"/>\n\
                                                <input type="hidden" class="add_marker_longitude" name="longitude" value="' + marker.getPosition().lng() + '"/>\n\
                                                <ul class="add_marker_listaDados">\n\
                                                    <li>Posição atual: (' + roundNumber(marker.getPosition().lat(), 6) + ', ' + roundNumber(marker.getPosition().lng(), 6) + ')</li>\n\
                                                </ul>\n\
                                                <hr>\n\
                                                <ul class="listaComandos">\n\
                                                    <li class="link_adcionar"><a href="javascript:document.newAPMap.submit();" class="jump-link" title="Adicionar Ponto de Acesso">Cadastrar</a></li>\n\
                                                    <li><a href="#" onclick="javascript: remove_marker('+ID+');" class="remover_ap" id="remove_ap" title="Remover"></a></li>\n\
                                                </ul>\n\
                                            </form>\n\
                                          </div>\n\
                                          ';

                        if (infowindow){
                            infowindow.open(map, marker);
                            infowindow.setContent(infowindowtext);
                        } else {
                            $(this).gmap3({
                                infowindow:{
                                    anchor:marker,
                                    options:{
                                        content: infowindowtext
                                    },
                                    events:{
                                        closeclick: function(infowindow){
                                            infowindow_active = null;
                                            marker_infowindow_active = null;
                                        }
                                    }
                                }
                            });
                        }
                    }
                },
                callback: function(marker){

                }
            }
        });
    }

    /*
     * Adciona um bal�o de informa��es no mapa
     */
    addinfowindow = function addInfoWindow(marker, data) {
        var map = $map.gmap3('get'),
        infowindow = $map.gmap3({
            get:{
                name:"infowindow"
            }
        });

        var LOCATION = data.location;
        var IP = data.IP;
        var MAC = data.MAC;
        var LISTTXPOWER = data.listTxPower;
        var UNDERLOADTHRESHOLD = data.underloadThreshold;
        var OVERLOADTHRESHOLD = data.overloadThreshold;
        var REGION = data.region;
        var REGIONNAME = data.regionName;
        var ENABLED = data.enabled;
        var REACHABLE = data.reachable;
        var ID = data.MAC;

        var STATUS = 1; //HABILITADO
        ENABLED_AP = ENABLED;

        switch(ENABLED) {
            case 0: //DESABILITADO
                STATUS = 0;
                break;
            case 1:
                if (REACHABLE == 0) {
                    STATUS = 2 //INCOMUNICAVEL
                }
                break;
        }

        var onclick_reiniciar = "";

        if (STATUS == 1) {
            onclick_reiniciar  = 'javascript: reiniciar_ap(\'' + ID + '\',' + REGION + ')';
        } else {
            onclick_reiniciar  = '';
        }

        var AP_MRTG = "";
        var IP_HOST = IP.split(".")[3];

        switch (IP_HOST.length) {
            case 1: AP_MRTG = "ap000" + IP_HOST;
                    break;

            case 2: AP_MRTG = "ap00" + IP_HOST;
                    break;

            case 3: AP_MRTG = "ap0" + IP_HOST;
                    break;

            case 4: AP_MRTG = "ap" + IP_HOST;
                    break;

            default: alert("Erro ao relacionar o IP deste AP com o Monitoramento. Favor contactar o administrador");
                    AP_MRTG = "";
                    break;
        }

        

        var infowindowtext = '<div class="listCommandAP_Map"><h4>Ponto de Acesso</h4>\n\
                                          <form method="POST" name="editAPMap" action="edit_APMap.jsf">\n\
                                              <input type="hidden" name="IP" value="' + IP + '"/>\n\
                                              <input type="hidden" name="MAC" value="' + MAC + '"/>\n\
                                              <input type="hidden" name="location" value="' + LOCATION + '"/>\n\
                                              <input type="hidden" name="listtxpower" value="' + LISTTXPOWER + '"/>\n\
                                              <input type="hidden" name="underloadThreshold" value="' + UNDERLOADTHRESHOLD + '"/>\n\
                                              <input type="hidden" name="overloadThreshold" value="' + OVERLOADTHRESHOLD + '"/>\n\
                                              <input type="hidden" name="region" value="' + REGION + '"/>\n\
                                              <input type="hidden" class="edit_marker_latitude" name="latitude" value="' + marker.getPosition().lat() + '"/>\n\
                                              <input type="hidden" class="edit_marker_longitude" name="longitude" value="' + marker.getPosition().lng() + '"/>\n\
                                              <ul class="listaDados">\n\
                                                 <li class="listCommandAP_Map_posicao_atual">Posição atual: (' + roundNumber(marker.getPosition().lat(), 6) + ', ' + roundNumber(marker.getPosition().lng(), 6) + ')</li>\n\
                                                 <li id="ip">IP: ' + IP + '</li>\n\
                                                 <li id="mac">MAC: ' + MAC + '</li>\n\
                                                 <li>Localização: ' + LOCATION + '</li>\n\
                                                 <li>Taxa de Potência: ' + LISTTXPOWER + '</li>\n\
                                                 <li>Limite de Carga Baixa: ' + UNDERLOADTHRESHOLD + '</li>\n\
                                                 <li>Limite de Sobrecarga: ' + OVERLOADTHRESHOLD + '</li>\n\
                                                 <li>Região: ' + REGIONNAME + '</li>\n\
                                                 <li id="enabled">' + ((STATUS>0) ? ((STATUS==1) ? 'Habilitado !':'Incomunicante !'):'Desabilitado !') + '</li>\n\
                                                 <li id="info_controller"><label>Comunicando com o controlador...</label></li>\n\
                                              </ul>\n\
                                              <hr>\n\
                                              <ul class="listaComandos">\n\
                                                 <li class="link_adcionar"><a href="javascript:document.editAPMap.submit();" class="jump-link" title="Editar Ponto de Acesso">Editar</a></li>\n\
                                          </form>\n\
                                                 <li><a href="' + URL_MRTG + 'index.php?page=devices&ap=' + AP_MRTG + '" target="_blank" class="ap_mrtg"></a></li>\n\
                                                 <li><a href="scan_info.jsf?MAC=' + ID + '&IP=' + IP + '&page=admin" class="scan_ap" title="Scanear Ponto de Acesso"></a></li>\n\
                                                 <li><a href="#" class="remover_ap" id="remove_ap" onclick="javascript: remove_AP(\'' + ID + '\');" title="Remover Ponto de Acesso"></a></li>\n\
                                                 <li><a href="#" id="reiniciar_ap" onclick="' + onclick_reiniciar + '" class="' + ((STATUS==1) ? "reiniciar_ap":"reiniciar_ap_desabilitado") + '" title="Reiniciar Ponto de Acesso"></a></li>\n\
                                                 <li><a href="#" onclick="javascript: atualiza_status(\'' + ID + '\',' + ENABLED + ',' + REGION + ');" id="atualiza_status" class="habilitado"><img src="../figuras/' + ((ENABLED==1) ? 'habilitado':'desabilitado') + '.png" title="' + ((ENABLED==1) ? 'Desabilitar':'Habilitar') + '"/></a></li>\n\
                                            </ul>\n\
                                          </div>';

        if (infowindow){
            infowindow.open(map, marker);
            infowindow.setContent(infowindowtext);
        } else {
            $map.gmap3({
                infowindow:{
                    anchor:marker,
                    options:{
                        content: infowindowtext
                    },
                    events:{
                        closeclick: function(infowindow){
                            infowindow_active = null;
                            marker_infowindow_active = null;
                        }
                    }
                }
            });
        }

        

        return infowindow;
    }

    /*
     * Adciona a posi��o especificada pelo mouse no painel do topo
     */
    function showCurrentPosition(position_current){
        $("#fixPanel").html("LAT : " + position_current.lat() +
            "<br />" +
            "LNG : " + position_current.lng());
    }

    /*
     * Adciona uma posi��o default para o mapa
     */
    function adcionarDefault(position_current, zoom){
        //CENTRALIZANDO O MAPA COM DADOS DEFAULT
        $.ajax({
            cache: false,
            type: "POST",
            url: "editMap.jsp",
            data: "type=0&lat=" + position_current.lat() + "&lng=" + position_current.lng() + "&zoom=" + zoom,
            dataType: "text",
            async: false,
            success: function (data) {
                if (data) {
                    alert("Posição default alterada com sucesso!", "SCIFI");
                } else {
                    alert("Erro ao alterar a posição default!");
                }
            }
        }).error(function () {
            alert("Erro ao alterar a posição default!");
            return false;
        });
    }

    /*
     * Atualiza automaticamente a posi��o do marcador, no banco de dados, ao ser arrastado no mapa
     */
    function updateDirectionMarker(position_current, MAC) {
        //ALTERANDO A LATITUDE E A LONGITUDE DO AP (MARCADOR)
        $.ajax({
            cache: false,
            type: "POST",
            url: "editMap.jsp",
            data: "type=1&lat=" + position_current.lat() + "&lng=" + position_current.lng() + "&MAC=" + MAC,
            dataType: "text",
            async: false,
            success: function (data) {
                if (data == false) {
                    alert("Erro ao alterar a posição!");
                }
            }
        }).error(function () {
            alert("Erro ao alterar a posição!");
            return false;
        });
    }
 
    menu.add('Marcar aqui', 'markHere',
        function(){
            incrementa_id_marcador_novo += 1;
            addMarker(incrementa_id_marcador_novo);
            menu.close();
        });
 
    menu.add('Centralizar aqui', 'target',
        function(){
            $map.gmap3('get').setCenter(current.latLng);
            menu.close();
        });

    menu.add('Mostrar posição', 'centerHere',
        function(){
            showCurrentPosition(current.latLng);
            menu.close();
        });

    menu.add('Adcionar posição como default', 'home',
        function(){
            adcionarDefault(current.latLng, $map.gmap3('get').getZoom());
            menu.close();
        });

    // INITIALIZE GOOGLE MAP
    $map.gmap3({
        map: {
            options:{
                center: center,
                zoom: zoom_default,
                mapTypeId: google.maps.MapTypeId.SATELLITE,
                mapTypeControl: true,
                mapTypeControlOptions: {
                    style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
                },
                navigationControl: true,
                scrollwheel: true,
                streetViewControl: true
            },
            events:{
                rightclick:function(map, event){
                    current = event;
                    menu.open(current);
                },
                click: function(){
                    menu.close();
                },
                dragstart: function(){
                    menu.close();
                },
                zoom_changed: function(){
                    menu.close();
                },
                bounds_changed: function(map){
                    $("#fixPanel").html("LAT : " + map.getCenter().lat() +
                        "<br />" +
                        "LNG : " + map.getCenter().lng());
                }
            }
        },
        panel: {
            options:{
                content:'<div id="fixPanel"></div>',
                top :true,
                center: true
            }
        },
        marker: {
            values: dados_ap,
            options:{
                draggable: true,
                animation: google.maps.Animation.DROP
            },
            events:{
                dragend: function(marker, event, context){
                    var data = context.data;
                    var MAC = data.MAC;
                    updateDirectionMarker(marker.getPosition(), MAC);

                    $(".edit_marker_latitude").val(marker.getPosition().lat());
                    $(".edit_marker_longitude").val(marker.getPosition().lng());
                    $(".listCommandAP_Map_posicao_atual").html("Posi��o atual: (" + roundNumber(marker.getPosition().lat(), 6) + ", " + roundNumber(marker.getPosition().lng(), 6) + ")");
                },
                click: function(marker, event, context){
                    infowindow_active = addinfowindow(marker, context.data);
                    marker_infowindow_active = marker;
                }
            }
        }
    });

    $map.gmap3({
        panel: {
            options:{
                content: '<div id="atualizar_status_aps"><a href="#">Atualizar APs</a></div>',
                top: true,
                right: true
            }
        }
    });

    $map.gmap3({
        panel: {
            options:{
                content:'<div id="fixPanel2" status="hide"></div>',
                middle: true,
                right: true
            }
        }
    });

    $map.gmap3({
        panel: {
            options:{
                content: '<div id="fixPanel3"></div>',
                middle: true,
                right: true
            }
        }
    });

    $.ajax({
        cache: false,
        type: "POST",
        url: "editMap.jsp",
        data: "type=9",
        dataType: "json",
        async: false,
        success: function (data) {
            regions = data;
        }
    }).error(function () {
        alert("Erro durante a consulta das Regiões para formar a SideTree do mapa.");
        regions = [];
    });

    $("#fixPanel2").click(function() {
        sidetree(regions, dados_ap);
    });

    $("#atualizar_status_aps a").click(function () {        
        atualiza_aps_map();
        
        //sidetree(regions, dados_ap);
        if(marker_infowindow_active != null) {
            google.maps.event.trigger(marker_infowindow_active, 'click', function () {}); //fecha e abre o bal�o de informa��es
        }
    });

    temporarizador_atualiza_aps();

    setInterval(function(){
        temporarizador_atualiza_aps(); //itera com tempo de 'temporarizador' (default � 60s)
    }, temporizador);
});

/*
 * Cria painel lateral do mapa
 */
function sidetree(regions, dados_ap) {
    if($("#fixPanel2").attr("status") == "hide") {
        $("#fixPanel3").fadeIn();
        $("#fixPanel3").css("visibility", "visible");

        fill_sidetree("#fixPanel3", "#tree", regions, dados_ap);
        
        $("#fixPanel2").css("background-image","url(../figuras/mapa/seta-next.png)");
        $("#fixPanel2").attr("status", "visible");
    } else {
        $("#fixPanel3").fadeOut();
        $("#fixPanel3").css("visibility","hidden");
        $("#fixPanel2").attr("status", "hide");
        $("#fixPanel2").css("background-image","url(../figuras/mapa/seta-prev.png)");
    }
}

/*
 * Preenche o painel lateral
 */
function fill_sidetree(panel, tree, regions, dados_ap) {
    var sidetree = "<div class=\"ui-widget-content\" id=\"sidetree\">";
        sidetree = sidetree + "<ul id=\"tree\" class=\"treeview\">";

        $.each(regions, function(key, val) {
            var id_region = val.value;
            var name_region = val.label;

            var expandable = "<li class=\"expandable\"><div class=\"hitarea expandable-hitarea\"></div><span>" + name_region + "</span>\n\
                                <ul style=\"display: none;\">";

            $.each(dados_ap, function(key, val) {
                if(id_region == val.data.region) {
                    expandable = expandable + "<li class=\"expandable\"><div class=\"hitarea expandable-hitarea\"></div><span id='treeview_" + (val.data.IP).split(".")[3] + "' class='treeview_" + ((val.data.enabled == 0) ? "disabled" : ((val.data.reachable == 1) ? "reachable":"unreachable")) + "'>" + val.data.IP + "</span>";
                    expandable = expandable + " <ul style=\"display: none;\">";
                    expandable = expandable + "     <li>" + val.data.MAC + "</li>";
                    expandable = expandable + "     <li>" + val.data.location + "</li>";
                    expandable = expandable + "     <li id='enabled_" + (val.data.IP).split(".")[3] + "'>" + ((val.data.enabled == 1) ? "Habilitado":"Desabilitado") + "</li>";
                    expandable = expandable + "     <li><a href=\"#\" class=\"ir_ap\" latitude=" + val.data.latitude + " longitude=" + val.data.longitude + " onclick=\"javascript: centraliza_ap_ir_para(" + val.data.latitude + ", " + val.data.longitude + ", '" + val.data.MAC + "')\">IR PARA</a></li>";
                    expandable = expandable + " </ul>";
                    expandable = expandable + "</li>";
                }
            });

            expandable = expandable + "</ul></li>";
            sidetree = sidetree + expandable;
        });

        sidetree = sidetree + "</ul></div>";

        $(panel).html("<div id='sidetreecontrol'><a href='#'>Contrair todos</a></div>");
        $(panel).append(sidetree);

        $(tree).treeview({
            collapsed: true, //TUDO CONTRAIDO
            animated: "fast",
            control:"#sidetreecontrol",
            prerendered: true,
            persist: "location"
        });
}

function update_sidetree(val) {
    var id_list_treeview = "#treeview_" + (val.IP).split(".")[3];
    var id_list_enabled = "#enabled_" + (val.IP).split(".")[3];

    switch(val.enabled) {
        case 0:
            $(id_list_treeview).attr("class", "treeview_disabled");
            $(id_list_enabled).html("Desabilitado");
            break;

        case 1:
            if (val.reachable == 1) {
                $(id_list_treeview).attr("class", "treeview_reachable");
            } else {
                $(id_list_treeview).attr("class", "treeview_unreachable");
            }

            $(id_list_enabled).html("Habilitado");

            break;
    }
}

function temporarizador_atualiza_aps() {
    $("#info_unreachable img").css("visibility","visible");
        atualiza_aps_map();
        setTimeout(function(){}, 2000);
    $("#info_unreachable img").css("visibility","hidden");

    if(marker_infowindow_active != null) {
        google.maps.event.trigger(marker_infowindow_active, 'click', function () {});
    }
}

function retorna_aps() {
    var data_aps = [];

    $.ajax({
        cache: false,
        type: "GET",
        url: "editMap.jsp",
        data: "type=5",
        dataType: "json",
        async: false,
        success: function (data) {
            data_aps = data;
        }
    }).error(function () {
        return null;
    });

    return data_aps;
}

/*
 * Atualiza APs no mapa, no painel lateral e na caixa de dialogo (link no topo da p�gina)
 */
function atualiza_aps_map() {
    $("body").attr("cursor","progress");
    var $map = $('#googleMap');
    var aps = retorna_aps();
    $("#info_unreachable a").html("");
    $("#info_unreachable label").html("");
    var counter = 0;
    var param = "";

    $.each(dados_ap, function(key, val_atual) {
        var MAC = val_atual.data.MAC;

        var marker = $map.gmap3({
            get: {
                id: MAC
            }
        });

        $.each(aps, function (key, val) {
            if(val.MAC == MAC) {
                var ENABLED = val.enabled;
                var REACHABLE = val.reachable;

                val_atual.data.enabled = ENABLED;
                val_atual.data.reachable = REACHABLE;

                switch(ENABLED) {
                    case 0: //DESABILITADO
                        marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/red-wifi.png'));

                        break;
                    case 1:  //HABILITADO
                        if (REACHABLE == 1) {
                            marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/green-wifi.png'));
                        } else { //HABILITADO PORÉM INCOMUNICANTE
                            marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/orange-wifi.png'));
                        }

                        break;
                }

                update_sidetree(val);

                if (REACHABLE == 0 && ENABLED == 1) {
                    counter++;

                    param = param + replaceAll($.trim("&IP"+counter+"=" + val.IP + "&MAC"+counter+"="+val.MAC+"&Region"+counter+"="+val.regionName+"&Enabled"+counter+"="+val.enabled+"&Loc"+counter+"="+val.location), " ", "+");
                }

                return false;
            }
        });
    });

    atualiza_cx_dialogo_aps_incomunicantes(counter, param);

    $("body").attr("cursor","default");
}

/*
 * Atualiza caixa de dialogo de APs incomunicantes
 */
function atualiza_cx_dialogo_aps_incomunicantes(counter, param) {
    if (counter > 0) {
        $("#info_unreachable label").html(" - ");

        if (counter > 1) {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " pontos de acesso incomunicáveis");
        } else {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " ponto de acesso incomunicável");
        }

        $("#info_unreachable a").attr("href","../admin/unreachable_aps.jsp?params_count=" + counter + "" + param + "&height=350&width=750");
    } else {
        $("#info_unreachable label").html("");
        $("#info_unreachable a").html("");
        $("#info_unreachable a").attr("href","#");
    }
}

function replaceAll(string, token, newtoken) {
    while (string.indexOf(token) != -1) {
        string = string.replace(token, newtoken);
    }
    return string;
}

/*
 * Ao clicar no link IR PARA do painel lateral, ele ativa esta fun��o, que centraliza o mapa no AP clicado e abre a infowindow
 */
function centraliza_ap_ir_para (latitude, longitude, MAC) {
    var $map = $('#googleMap');
    $map.gmap3('get').setCenter(new google.maps.LatLng(latitude, longitude));
    $map.gmap3('get').setZoom(20);

    var marker = $map.gmap3({
        get: {
            id: MAC
        }
    });

    var context = [];

    $.each(dados_ap, function(key, val) {
        if(val.data.MAC == MAC) {
            context = val.data;
        }
    });

    addinfowindow(marker, context);
}

function replacePointToEmpty(string, token, newtoken) {
    while (string.indexOf(token) != -1) {
        string = string.replace(token, newtoken);
    }
    return string;
}

function reiniciar_ap(id, region) {
    var confirma_reinicio = confirm("Tem certeza que deseja reiniciar este Ponto de Acesso (" + id + ") ?");

    if (!confirma_reinicio) {
        return false;
    }

    $.ajax({
        cache: false,
        type: "POST",
        url: "editMap.jsp",
        data: "type=8&id=" + id + "&region=" + region,
        dataType: "text",
        async: false,
        success: function (data) {

            if (data) {

                var reachable = retorna_reachable(id);
                var enabled = 0;
                var region = 0;

                if (reachable >= 0) {
                    var $map = $('#googleMap');
                    var marker = $map.gmap3({
                        get: {
                            id: id
                        }
                    });

                    $.each(dados_ap, function(key, val) {
                        if (val.id == id) {
                            val.data.reachable = reachable;
                            enabled = val.data.enabled;
                            region = val.data.region;

                            return false;
                        }
                    });

                    atualiza_info_aps(enabled, marker, reachable, id, region);

                    if(enabled == 1) {
                        if (reachable == 1) {
                            alert("Foi iniciado o processo de reinício do Ponto de Acesso!\n\nEle será reiniciado em instantes.");
                        } else {
                            alert("Reinício do Ponto de Acesso falhou: ele está incomunicante!");
                        }
                    }
                }
            } else if (!data) {
                alert("Erro ao atualizar status do Ponto de Acesso!\n\nUma falha na comunicação com o Controlador, ou com o Ponto de Acesso, causou este problema.");
            }
        }
    }).error(function () {
        alert("Erro ao reiniciar Ponto de Acesso!");
    });

    return true;
}

/*
 * Remove marcador
 */
function remove_marker(id) {
    var $map = $('#googleMap');
    $map.gmap3({
        clear: {
            id: id
        }
    });
}

function remove_AP(id) {
    var confirma_exclusao = confirm("Tem certeza que deseja excluir este Ponto de Acesso (" + id + ") ?");

    if (!confirma_exclusao) {
        return false;
    }

    $.ajax({
        cache: false,
        type: "POST",
        url: "editMap.jsp",
        data: "type=3&id=" + id,
        dataType: "text",
        async: false,
        success: function (data) {

            if (data == 1) {

                remove_marker(id);

                infowindow_active = null;
                marker_infowindow_active = null;

                var indice = 0;
                $.each(dados_ap, function (key, val) {
                    if(val.id == id) {
                        indice = key;

                        return false;
                    }
                });

                dados_ap.splice(indice, 1);
                
                return true;
                
            } else {
                alert("Erro ao excluir Ponto de Acesso!");
                return false;
            }
        }
    }).error(function () {
        alert("Erro ao excluir Ponto de Acesso!");
        return false;
    });

    return true;
}

function retorna_reachable(id) {
    var reachable = 0;

    $.ajax({
        cache: false,
        type: "POST",
        url: "editMap.jsp",
        data: "type=7&id="+id,
        dataType: "text",
        async: false,
        success: function (data) {
            if (data >= 0) {
                reachable = data;
            } else {
                alert("Erro ao retornar Status Reachable !");
                reachable = -1;
            }
        }
    }).error(function () {
        reachable = -1;
        alert("Erro ao retornar Status Reachable !");
    });

    return reachable;
}

function blink(selector) {
    $(selector).fadeOut('slow', function() {
        $(this).fadeIn('slow', function() {
            blink(this);
        });
    });
}

function atualiza_status(id, enabled, region) {
    switch(enabled) {
        case 0:
            enabled=1;
            break;
        case 1:
            enabled=0;
            break;
    }

    $.ajax({
        cache: false,
        type: "POST",
        url: "editMap.jsp",
        data: "type=4&id=" + id + "&enabled=" + enabled + "&region=" + region,
        dataType: "text", //boolean
        async: false,
        success: function (data) {
            
            if (data==1) {
                var $map = $('#googleMap');
                var marker = $map.gmap3({
                    get: {
                        id: id
                    }
                });

                var reachable = retorna_reachable(id);

                if (reachable >= 0) {
                    $.each(dados_ap, function(key, val) {
                        if (val.id == id) {
                            val.data.reachable = reachable;
                            val.data.enabled = enabled;
                        }
                    });

                    atualiza_info_aps(enabled, marker, reachable, id, region);
                }
            } else {
                alert("Erro ao atualizar status do Ponto de Acesso!\n\nUma falha na comunicação com o Controlador, ou Banco de Dados, causou este problema.");
            }
        }
    }).error(function () {
        alert("Erro ao atualizar status do Ponto de Acesso!");
    });
    
//$("#info_controller label").css("visibility","hidden");
//$("#info_controller").css("margin-top","0px");
}

function atualiza_info_aps(enabled, marker, reachable, id, region) {

    switch(enabled) {

        case 0:
            $("#atualiza_status img").attr("src", "../figuras/desabilitado.png");
            $("#atualiza_status img").attr("title", "Habilitar");
            $("#enabled").html("Desabilitado !");
            ENABLED_AP = 0;
            $("#atualiza_status").attr("onclick","javascript: atualiza_status('" + id + "'," + 0 + "," + region + ")");
            marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/red-wifi.png'));

            $("#reiniciar_ap").attr("onclick", "");
            $("#reiniciar_ap").attr("class", "reiniciar_ap_desabilitado");
            break;

        case 1:
            $("#atualiza_status img").attr("src", "../figuras/habilitado.png");
            $("#atualiza_status img").attr("title", "Desabilitar");
            $("#atualiza_status").attr("onclick","javascript: atualiza_status('" + id + "'," + 1 + "," + region + ")");

            if (reachable == 1) {
                $("#enabled").html("Habilitado !");
                $("#reiniciar_ap").attr("onclick", 'javascript: reiniciar_ap(\'' + id + '\',' + region + ')');
                $("#reiniciar_ap").attr("class", "reiniciar_ap");
                marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/green-wifi.png'));
            } else {
                $("#enabled").html("Incomunicante !");
                $("#reiniciar_ap").attr("onclick", "");
                $("#reiniciar_ap").attr("class", "reiniciar_ap_desabilitado");
                marker.setIcon(new google.maps.MarkerImage('../figuras/mapa/orange-wifi.png'));
            }

            ENABLED_AP = 1;
            break;
    }
}

function roundNumber(number, decimals) { // Arguments: number to round, number of decimal places
    var newnumber = new Number(number+'').toFixed(parseInt(decimals));

    return parseFloat(newnumber); // Output the result to the form field (change for your purposes)
}