/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Classe que contém métodos de validação dos valores dos parâmetros do controlador inseridos via interface de usuário.
 * @author controlador
 */
public class JPropertyValidator extends JValidator
{
    /**
     * Método que verifica se o valor preenchido via interface de usuário para um determinado parâmetro do controlador é válido. Apenas números são aceitos.
     * @param context Contexto do Faces.
     * @param component Componente que está sendo validado.
     * @param value Valor do componente que está sendo validado.
     */
    public void isValueValid(FacesContext context, UIComponent component, Object value)
    {
        Map<String, Object> hashAttributes = component.getAttributes();
        
        String strType = (String)hashAttributes.get("Type");
        
        if(!isEmpty(context, component, value) && (strType != null))
        {
            if(strType.equals("IP"))
            {
                validateIP(context, component, value);
            }
            else
            {
                if(strType.equals("UINT") || strType.equals("SECONDS"))
                {
                    isUnsignedInteger(context, component, value);
                }
                else
                {
                    if(strType.equals("PERCENTAGE"))
                    {
                        isPercentage(context, component, value);
                    }
                }
            }
        }
    }

    private void validateIP(FacesContext context, UIComponent component, Object value)
    {
        if(!isIPValid((String)value))
        {
            showMessage(component, context, "IP Inválido", SHORT_MESSAGE, FILL_DATA_MESSAGE);
        }
    }
}
