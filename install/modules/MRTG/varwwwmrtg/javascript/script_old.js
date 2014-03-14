$(document).ready(function(){
    $(".aps").click(function () {
        $("#folders").load($(this).attr("path"));
    });
});