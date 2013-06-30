/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/**
 * Classe que cont�m m�todos de valida��o gen�ricos para valores inseridos via interface de usu�rio.
 * @author controlador
 */
public class JValidator
{

    protected final String FILL_DATA_MESSAGE = "* Por favor, preencha os dados corretamente";
    protected final String SHORT_MESSAGE = "*";
    private static final String ONLY_NUMBER = "Apenas d�gitos de 0 a 9 s�o aceitos.";
    private static final String ONLY_NUMBER_FULL = "* " + ONLY_NUMBER;

    /**
     * Verifica se um campo necess�rio n�o foi preenchido.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o campo n�o foi preenchido e false, caso contr�rio.
     */
    public boolean isEmpty(FacesContext context, UIComponent component, Object value)
    {
        String strValue = (String) value;

        if (strValue.isEmpty())
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return true;
        }

        return false;
    }

    /**
     * Verifica se o valor de um campo � um inteiro maior que zero.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o valor do campo � um inteiro maior que zero e false, caso contr�rio.
     */
    public boolean isUnsignedInteger(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return false;
        }
        else
        {
            String strValue = String.valueOf(value);

            Pattern patternNumber = Pattern.compile("\\d*");
            Matcher matcher = patternNumber.matcher(strValue);

            if (!matcher.matches())
            {
                ((UIInput) component).setValid(false);
                showMessage(component, context, ONLY_NUMBER, SHORT_MESSAGE, ONLY_NUMBER_FULL);
            }
            else
            {
                if (Integer.valueOf(strValue) < 0)
                {
                    ((UIInput) component).setValid(false);

                    showMessage(component, context, "Este valor deve ser um inteiro positivo.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Verifica se o email informado está correto.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o valor do campo � um inteiro maior que zero e false, caso contr�rio.
     */
    public boolean isEmail(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return false;
        }
        else
        {
            String enteredEmail = String.valueOf(value);
            Pattern patternNumber = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher matcher = patternNumber.matcher(enteredEmail);

            if (!matcher.matches())
            {
                ((UIInput) component).setValid(false);
                showMessage(component, context, "Este e-mail deve ser válido.", SHORT_MESSAGE, "* Este e-mail deve ser válido.");

                return false;
            }
        }

        return true;
    }

    /**
     * M�todo que apresenta mensagens na tela. Evita que mensagens repetidas sejam mostradas.     
     * @param component Componente que est� sendo validado.
     * @param context Contexto do Faces.
     * @param strFullMessage Mensagem comprida (com muito texto).
     * @param strShortMessage Mensagem curta (com 1 caracter).
     * @param strFillDataMessage Mensagem que complementa o significado da mensagem curta.
     */
    protected void showMessage(UIComponent component, FacesContext context, String strFullMessage, String strShortMessage, String strFillDataMessage)
    {
        FacesMessage msg;
        //verifica se � para exibir ou n�o a mensagem curta
        String strValue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("EditForm:ShortErrorMessage");
        // se � para exibir a mensagem curta,
        if ((strValue != null) && (strValue.equals("true")))
        {
            // cria uma mensagem curta
            msg = new FacesMessage(strShortMessage);
            // pega a lista de mensagens que est�o sendo apresentadas.
            List<FacesMessage> list = context.getMessageList(null);

            //percorre a lista de mensagens para saber se ela j� est� sendo exibida.
            //Se estiver, n�o precisa exib�-la novamente
            int nInd;
            for (nInd = 0; nInd < list.size(); nInd++)
            {
                // se a mensagem strFillDataMessage j� foi exibida,
                if (list.get(nInd).getDetail().equals(strFillDataMessage))
                {
                    break;
                }
            }

            //se n�o achou a mensagem na lista,
            if (nInd == list.size())
            {
                // adiciona a mensagem strFillDataMessage na lista
                context.addMessage(null, new FacesMessage(strFillDataMessage));
            }
        }
        // se n�o � para exibir a mensagem curta,
        else
        {
            // cria uma mensagem longa
            msg = new FacesMessage(strFullMessage);
        }
        // apresenta a mensagem (longa ou curta)
        context.addMessage(component.getClientId(context), msg);
    }

    /**
     * M�todo que verifica se o string possui o formato IP
     * 
     * @param strIP String contendo o IP
     * @return Retorna true se o string possui o formato de um IP v�lido e falso, caso contr�rio.
     */
    protected boolean isIPValid(String strIP)
    {
        Pattern patternIP = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        Matcher matcher = patternIP.matcher(strIP);

        return matcher.matches();
    }

    protected UIComponent findComponent(UIComponent component, String id)
    {
        if (id.equals(component.getId()))
        {
            return component;
        }

        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext())
        {
            UIComponent found = findComponent(kids.next(), id);
            if (found != null)
            {
                return found;
            }
        }

        return null;
    }

    /**
     * Verifica se o valor de um campo � um double positivo.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o valor do campo � um double ou false, caso contr�rio.
     */
    public boolean isDoublePositive(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return false;
        }
        else
        {
            String strValue = String.valueOf(value);

            Pattern patternNumber = Pattern.compile("\\d+(\\.\\d+)*");
            Matcher matcher = patternNumber.matcher(strValue);

            if (!matcher.matches())
            {
                ((UIInput) component).setValid(false);
                showMessage(component, context, "Este valor deve ser um número real.", SHORT_MESSAGE, "* Este valor deve ser um número real.");

                return false;
            }
        }

        return true;
    }

    /**
     * Verifica se o valor de um campo � um double (positivo ou negativo).
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o valor do campo � um double ou false, caso contr�rio.
     */
    public boolean isDouble(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return false;
        }
        else
        {
            String strValue = String.valueOf(value);

            Pattern patternNumber = Pattern.compile("-?\\d+(\\.\\d+)*");
            Matcher matcher = patternNumber.matcher(strValue);

            if (!matcher.matches())
            {
                ((UIInput) component).setValid(false);
                showMessage(component, context, "Este valor deve ser um número real (O operador + não precisa ser posto).", SHORT_MESSAGE, "* Este valor deve ser um número real (O operador + não precisa ser posto).");

                return false;
            }
        }

        return true;
    }

    /**
     * Verifica se o valor de um campo � uma porcentagem v�lida.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     * @return Retorna true se o valor do campo � uma porcentagem v�lida ou false, caso contr�rio.
     */
    public boolean isPercentage(FacesContext context, UIComponent component, Object value)
    {
        if (value == null)
        {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Este valor deve ser preenchido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);

            return false;
        }
        else
        {
            if (isDoublePositive(context, component, value))
            {
                Double dValue = Double.valueOf(String.valueOf(value));

                if (dValue < 0.0 || dValue > 100.0)
                {
                    ((UIInput) component).setValid(false);

                    showMessage(component, context, "Este valor deve estar entre 0 e 100.", SHORT_MESSAGE, "* Este valor deve estar entre 0 e 100.");

                    return false;
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
