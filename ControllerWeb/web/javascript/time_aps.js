function trocaCursor(tipo, btn){
    this.document.body.style.cursor=tipo;
    btn.style.cursor="url(../figuras/wait.gif)";

    return true;
}

var data_aps = [];

$(document).ready(function () {
    temporarizador_atualiza_time_aps(temporizador);
});

function retorna_aps_time_aps() {
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

function temporarizador_atualiza_time_aps(time) {
    $("#info_unreachable img").css("visibility","visible");
    atualiza_time_aps();
    setTimeout(function(){}, 2000);
    $("#info_unreachable img").css("visibility","hidden");

    setInterval(function(){
        $("#info_unreachable img").css("visibility","visible");
        atualiza_time_aps();
        $("#info_unreachable img").css("visibility","hidden");
    }, time);
}

function atualiza_time_aps(){
    var aps = retorna_aps_time_aps();

    change_label_attention_time_aps(aps);
}

function change_label_attention_time_aps(aps) {
    $("#info_unreachable a").html("");
    $("#info_unreachable label").html("");
    var counter = 0;
    var param = "";

    $.each(aps, function (key, val) {
        var REACHABLE = val.reachable;
        var ENABLED = val.enabled;

        if (REACHABLE == 0 && ENABLED == 1) {
            counter++;
            param = param + replaceAll($.trim("&IP"+counter+"=" + val.IP + "&MAC"+counter+"="+val.MAC+"&Region"+counter+"="+val.regionName+"&Enabled"+counter+"="+val.enabled+"&Loc"+counter+"="+val.location), " ", "+");
        }
    });

    if (counter > 0) {
        $("#info_unreachable label").html(" - ");

        if (counter > 1) {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " pontos de acesso incomunicantes !");
        } else {
            $("#info_unreachable a").html("ATENÇÃO: Há " + counter + " ponto de acesso incomunicantes !");
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

