/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var page = "";

function getPreviousLink()
{    
    var queryString = window.top.location.search.substring(1);
    page = getParameter(queryString, "page");    
}

$(document).ready(function(){    
    $("#bttVoltar").click(function() {
        window.location.href = page + '.jsf';
    });
});