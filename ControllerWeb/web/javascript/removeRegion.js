/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ShowRemovalStatus()
{    
    var queryString = window.top.location.search.substring(1);
    var added = getParameter(queryString, "removed");

    if(added == 1)
    {
        alert("Região removida com sucesso.");
    }
    else
    {
        if(added == 0)
        {
            alert("A região não foi removida. Certifique-se de que não há nehum ponto de acesso associado a esta região.");
        }
    }
}



