/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ShowUpdateStatus()
{
    var queryString = window.top.location.search.substring(1);
    var edited = getParameter(queryString, "edited");

    if(edited == 1)
    {
        alert("Ponto de acesso editado com sucesso.");
    }
    else
    {
        if(edited == 0)
        {
            alert("O ponto de acesso não foi editado.");
        }
    }
}

