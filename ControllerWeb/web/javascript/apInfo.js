/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function ConfirmRemoval()
{
    var answer;

    answer = confirm("Tem certeza que deseja excluir?");

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

    answer = confirm("Tem certeza que deseja reiniciar o Ponto de Acesso?");

    if (answer)
    {
        return true;
    }
    else
    {
        return false;
    }
}