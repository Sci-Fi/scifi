/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ConfirmRemoval()
{
    var answer;

    answer = confirm("Tem certeza que deseja excluir?", "SCIFI");

    if (answer)
    {
        return true;
    }
    else
    {
        return false;
    }
}

function ConfirmReboot()
{
    var answer;

    answer = confirm("Tem certeza que deseja reiniciar o Ponto de Acesso?", "SCIFI");

    if (answer)
    {
        return true;
    }
    else
    {
        return false;
    }
}

var dt_aps = [];
var data;
var HABILITADO = 1;
var DESABILITADO = 0;

$(document).ready(function(){
    temporarizador_atualiza_aps_apinfo(temporizador);
});

/*
 * Retorna os AP's da APInfo
 */
function retorna_aps_apinfo() {
    var data_aps;

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

function atualiza_aps_apinfo(){
    var aps = retorna_aps_apinfo();

    change_label_attention_aps_apinfo(aps);

    return aps;
}

/*
 * Altera a label de avisos de AP's incomunicantes
 */
function change_label_attention_aps_apinfo(aps) {
    $("#info_unreachable a").html("");
    $("#info_unreachable label").html("");
    var counter = 0;
    var param = "";

    $.each(aps, function (key, val) {
        var REACHABLE = val.reachable;
        var ENABLED = val.enabled;

        if (REACHABLE == 0 && ENABLED == 1) {
            counter++;
            //Cria��o dos par�metros
            param = param + replaceAll($.trim("&IP"+counter+"=" + val.IP + "&MAC"+counter+"="+val.MAC+"&Region"+counter+"="+val.regionName+"&Enabled"+counter+"="+val.enabled+"&Loc"+counter+"="+val.location), " ", "+");
        }
    });
    
    if (counter > 0) {
        $("#info_unreachable label").html(" - ");

        if (counter > 1) {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " pontos de acesso incomunicantes !");
        } else {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " ponto de acesso incomunicante !");
        }

        //Par�metros sendo enviados pela URL
        $("#info_unreachable a").attr("href","../admin/unreachable_aps.jsp?params_count=" + counter + "" + param + "&height=350&width=750");
    } else {
        $("#info_unreachable label").html("");
        $("#info_unreachable a").html("");
        $("#info_unreachable a").attr("href","#");
    }
}

/*
 * Temporizador dos avisos de AP's incomunicantes. A cada 60 segundos, o sistema busca no banco de dados uma lista de APs incomunicantes.
 */
function temporarizador_atualiza_aps_apinfo(time) {
    $("#info_unreachable img").css("visibility","visible");
        var data = atualiza_aps_apinfo();
        update_table_apinfo(data);
        setTimeout(function(){}, 2000);
    $("#info_unreachable img").css("visibility","hidden");

    setInterval(function(){
        $("#info_unreachable img").css("visibility","visible");
        data = atualiza_aps_apinfo();
        update_table_apinfo(data);
        $("#info_unreachable img").css("visibility","hidden");
    }, time);
}

/*
 * Atualiza a tabela da pagina ap_info.jsf
 */
function update_table_apinfo(data_aps) {
    var STATUS_LOW = 0;
    var STATUS_NORMAL = 1;
    var STATUS_FULL = 2;

    $('#tab_aps').dataTable().fnClearTable();

    //MONTA A TABELA
    $.each(data_aps, function(key, val) {
        var onclick_reboot = "";

        if ((val.enabled == 1) && (val.reachable == 1)) {
            onclick_reboot = 'javascript: reboot_ap(\'' + val.MAC + '\', ' + val.region + ');';
        } else {
            onclick_reboot = '';
        }

        //Adciona linhas na tabela
        $("#tab_aps tbody").append('<tr id="ap_' + key + '">\n\
                <td>'+ val.MAC +'</td>\n\
                <td>'+ val.IP +'</td>\n\
                <td>'+ val.location +'</td>\n\
                <td>'+ val.regionName +'</td>\n\
                <td>'+ val.channel +'</td>\n\
                <td>'+ val.listTxPower +'</td>\n\
                <td>'+ val.curTxPower +'</td>\n\
                <td>'+ val.numberOfUsers +'</td>\n\
                <td>'+ loadStatusText(val.loadStatus) +'</td>\n\
                <td>'+ val.underloadThreshold +'</td>\n\
                <td>'+ val.overloadThreshold +'</td>\n\
                <td>'+ val.latitude +'</td>\n\
                <td>'+ val.longitude +'</td>\n\
                <td><div class="visualizarScan">\n\
                        <ul>\n\
                            <li>\n\
                                <a href="scan_info.jsf?MAC=' + val.MAC + '&IP=' + val.IP + '&page=ap_info" target="_self" title="Visualizar Informações de Scan"></a>\n\
                            </li>\n\
                        </ul>\n\
                    </div>\n\
                </td>\n\
                <td>\n\
                    <input type="button" onclick="javascript: remove_ap(\'' + val.MAC + '\', ' + key + ');" title="Remover" class="remover"/>\n\
                    <input type="button" onclick="' + onclick_reboot + '" id="reboot_ap_' + key + '" title="Reiniciar" class="' + (((val.enabled == 1) && (val.reachable == 1)) ? "reiniciar":"reiniciar_desabilitado") + ' disabled="' + (((val.enabled == 1) && (val.reachable == 1)) ? false:true) + '"/>\n\
                    <input type="button" onclick="javascript: update_enabled(this, \'' + val.MAC + '\', ' + val.enabled + ', ' + val.reachable + ', ' + val.region + ', ' + key + ');" id="update_enabled_' + key + '" title="' + ((val.enabled) ? 'Desabilitar':'Habilitar') + '" class="' + ((val.enabled) ? 'desabilitar':'habilitar') + '"></a>\n\
                </td>\n\
            </tr>');
    });

    var start_display = $('#tab_aps').dataTable().fnSettings()._iDisplayStart; //RESGATA A PÁGINA DO DATATABLE QUE O USUÁRIO ESTÁ
    var start_display_length = $('#tab_aps').dataTable().fnSettings()._iDisplayLength; //RESGATA A PÁGINA DO DATATABLE QUE O USUÁRIO ESTÁ

    //Reinicializando a datatable
    $('#tab_aps').dataTable({
        "iDisplayStart": start_display,
        "iDisplayLength": start_display_length,
        "bProcessing": true,
        "bPaginate": true,
        "bJQueryUI": false,
        "bFilter": true,
        "bAutoWidth": false,
        "bSort": false,
        "sPaginationType": "full_numbers",
        "bInfo": false,
        "bInfoFiltered": false,
        "bInfoEmpty": false,
        "bEmptyTable": true,
        "bZeroRecords": true,
        "fnDrawCallback": function(oSettings) {
            $("#tab_aps > tbody > tr").removeClass(); //REMOVENDO TODAS AS CLASSES DE TODAS AS LINHAS, PARA COLORI-LAS

            $.each(data_aps, function(key, val) {
                change_color_line($("#ap_" + key), val.enabled, val.reachable); //COLORINDO CADA LINHA, DISTINGUINDO O AP DESABILITADO, INCOMUNICANTE E HABILITADO
            });
        },
        "bDestroy": true,
        "bRetrieve": false
    });

    function loadStatusText(loadStatus) {
        var loadStatusText = "";

        switch (loadStatus)
        {
            case STATUS_LOW:
                loadStatusText = "Carga Baixa";
                break;

            case STATUS_NORMAL:
                loadStatusText = "Carga Média";
                break;

            case STATUS_FULL:
                loadStatusText = "Sobrecarregado";
                break;
        }

        return loadStatusText;
    }
}

/*
 * Remove linha da tabela
 */
function tr_remove(key) {
    var table = $('#tab_aps').dataTable();
    
    var pos = table.fnGetPosition($('#ap_' + key).get(0));
    table.fnDeleteRow(pos);
}

function remove_ap(id, key) {

    var confirma_exclusao = confirm("Tem certeza que deseja excluir este Ponto de Acesso (" + id + ") ?", "SCIFI");

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
                alert("Ponto de Acesso excluido com sucesso!", "SCIFI");
                tr_remove(key);
                return true;

            } else {
                alert("Erro ao excluir Ponto de Acesso!", "SCIFI");
                return false;
            }
        }
    }).error(function () {
        alert("Erro ao excluir Ponto de Acesso!", "SCIFI");
        return false;
    });

    return true;
}

function reboot_ap(id, region) {
    var confirma_reinicio = confirm("Tem certeza que deseja reiniciar este Ponto de Acesso (" + id + ") ?", "", "SCIFI");

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
                alert("Foi iniciado o processo de reinício do Ponto de Acesso!\n\nEle será reiniciado em instantes.", "SCIFI");
            } else if (!data) {
                alert("Erro ao atualizar status do Ponto de Acesso!\n\nUma falha na comunicação com o Controlador, ou com o Ponto de Acesso, causou este problema.", "SCIFI");
            }
        }
    }).error(function () {
        alert("Erro ao reiniciar Ponto de Acesso!", "SCIFI");
    });

    return true;
}

function update_enabled(button, id, enabled, reachable, region, key) {

    switch(enabled) {
        case 0: //0 -> 1
            enabled=1;
            break;
        case 1: //1 -> 0
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
                change_info_aps(id, enabled, reachable, region, key);
            } else {
                alert("Erro ao atualizar status do Ponto de Acesso!\nUma falha na comunicação com o Controlador, ou Banco de Dados, causou este problema.", "SCIFI");
            }
        }
    }).error(function () {
        alert("Erro ao atualizar status do Ponto de Acesso!", "SCIFI");
    });

}

function change_info_aps(id, enabled, reachable, region, key) {

    switch(enabled) {

        case DESABILITADO:
            $("#update_enabled_" + key).attr("class", "habilitar");
            
            $("#update_enabled_" + key).attr("title", "Habilitar");
            $("#update_enabled_" + key).attr("onclick","javascript: update_enabled(this, '" + id + "', " + 0 + ", " + reachable + ", " + region + ", " + key + ")");

            change_color_line("#ap_" + key, enabled, reachable);

            $("#reboot_ap_" + key).attr("class", "reiniciar_desabilitado");
            $("#reboot_ap_" + key).attr("disabled", true);
            break;

        case HABILITADO:
            $("#update_enabled_" + key).attr("class", "desabilitar");

            $("#update_enabled_" + key).attr("title", "Desabilitar");
            $("#update_enabled_" + key).attr("onclick","javascript: update_enabled(this, '" + id + "', " + 1 + ", " + reachable + ", " + region + ", " + key + ")");

            if (reachable == 1) {
                $("#reboot_ap_" + key).attr("class", "reiniciar");
                $("#reboot_ap_" + key).attr("disabled", false);

                change_color_line("#ap_" + key, enabled, reachable);
            } else {
                $("#reboot_ap_" + key).attr("class", "reiniciar_desabilitado");
                $("#reboot_ap_" + key).attr("disabled", true);

                change_color_line("#ap_" + key, enabled, reachable);
            }

            break;
    }
}

function change_color_line(line, enabled, reachable) {
    switch(enabled) {
        case DESABILITADO:
            $(line).attr("class", "disabled");
            break;

        case HABILITADO:
            if (reachable == 1) {
                $(line).attr("class", "enabled");
            } else {
                $(line).attr("class", "unreachable");
            }
            break;
    }
}

function replaceAll(string, token, newtoken) {
    while (string.indexOf(token) != -1) {
        string = string.replace(token, newtoken);
    }
    return string;
}

window.onload = init;

function init() {
    if($.browser.mozilla) {
        $("#tab_aps_wrapper").addClass("mozilla"); //.css("clear", "right");
    }
}

