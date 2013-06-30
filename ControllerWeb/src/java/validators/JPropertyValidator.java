/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Classe que cont�m m�todos de valida��o dos valores dos par�metros do controlador inseridos via interface de usu�rio.
 * @author controlador
 */
public class JPropertyValidator extends JValidator
{

    /**
     * M�todo que verifica se o valor preenchido via interface de usu�rio para um determinado par�metro do controlador � v�lido. Apenas n�meros s�o aceitos.
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void isValueValid(FacesContext context, UIComponent component, Object value)
    {
        Map<String, Object> hashAttributes = component.getAttributes();

        String strType = (String) hashAttributes.get("Type");

        if (!isEmpty(context, component, value) && (strType != null))
        {
            if (strType.equals("IP"))
            {
                validateIP(context, component, value);
            }
            else
            {
                if (strType.equals("UINT") || strType.equals("SECONDS"))
                {
                    isUnsignedInteger(context, component, value);
                }
                else
                {
                    if (strType.equals("PERCENTAGE"))
                    {
                        isPercentage(context, component, value);
                    }
                    else
                    {
                        if (strType.equals("DECIMAL"))
                        {
                            isDouble(context, component, value);
                        }
                        else
                        {
                            if (strType.equals("STRING"))
                            {
                                isEmpty(context, component, value);
                            }
                            else
                            {
                                if (strType.equals("EMAIL"))
                                {
                                    isEmail(context, component, value);
                                }                                
                            }
                        }
                    }
                }
            }
        }
    }

    private void validateIP(FacesContext context, UIComponent component, Object value)
    {
        if (!isIPValid((String) value))
        {
            showMessage(component, context, "IP Inválido", SHORT_MESSAGE, FILL_DATA_MESSAGE);
        }
    }
}
