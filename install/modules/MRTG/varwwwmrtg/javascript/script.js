$(document).ready(function(){

    $("#tabelaAP").dataTable({
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
        "bZeroRecords": true	
    });

    $("#tabelaMRTG").dataTable({
        "bProcessing": true,
        "bPaginate": false,
        "bJQueryUI": false,
        "bFilter": false,
        "bAutoWidth": false,
        "bSort": false,
        "bInfo": false,
        "bInfoFiltered": false
    });

    var name_ap = $("#tabelaMRTG").attr("name").toLowerCase();
	
    $(".mrtg_graphs_network").each(function () {
	$(this).attr("href", name_ap + "/" + $(this).attr("href"));
        $(this).find("img").attr("src", name_ap + "/" + $(this).find("img").attr("src"));
    });

    $(".mrtg_graphs_cpu").attr("href", name_ap + "/" + $(".mrtg_graphs_cpu").attr("href"));
    $(".mrtg_graphs_cpu img").attr("src", name_ap + "/" + $(".mrtg_graphs_cpu img").attr("src"));

    $(".mrtg_graphs_mem").attr("href", name_ap + "/" + $(".mrtg_graphs_mem").attr("href"));
    $(".mrtg_graphs_mem img").attr("src", name_ap + "/" + $(".mrtg_graphs_mem img").attr("src"));
    
    $(".mrtg_graphs_usu").attr("href", name_ap + "/" + $(".mrtg_graphs_usu").attr("href"));
    $(".mrtg_graphs_usu img").attr("src", name_ap + "/" + $(".mrtg_graphs_usu img").attr("src"));

    $(".mrtg_graphs_dsk").attr("href", name_ap + "/" + $(".mrtg_graphs_dsk").attr("href"));
    $(".mrtg_graphs_dsk img").attr("src", name_ap + "/" + $(".mrtg_graphs_dsk img").attr("src"));
});
