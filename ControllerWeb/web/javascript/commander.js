/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ShowExecutionStatus()
{    
    var queryString = window.top.location.search.substring(1);
    var answer = getParameter(queryString, "answer");

    if(answer == 1)
    {
        alert("Comando executado com sucesso.");
    }
    else
    {
        if(answer == 0)
        {
            alert("O comando não pôde ser executado.");
        }
    }
}

