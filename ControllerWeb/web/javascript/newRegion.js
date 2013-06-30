/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ShowInsertionStatus()
{    
    var queryString = window.top.location.search.substring(1);
    var added = getParameter(queryString, "added");

    if(added == 1)
    {
        alert("Região adicionada com sucesso.");
    }
    else
    {
        if(added == 0)
        {
            alert("A região não foi adicionada.");
        }
    }
}

