/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import database.JAPInfoDBManager;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author ferolim
 */
public class JRegionValidator extends JValidator
{
    /**
     * Método que valida o nome da Região, analisando se foi preenchido e é único. 
     * @param context Contexto do Faces.
     * @param component Componente que está sendo validado.
     * @param value Valor do componente que está sendo validado.
     */
    public void checkRegionName(FacesContext context, UIComponent component, Object value)
    {
        if (!isEmpty(context, component, value))
        {
            String strRegionName = (String) value;
                           
            if(!isFilenameValid(strRegionName))
            {
                ((UIInput) component).setValid(false);
                showMessage(component, context, "Nome de região inválido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
                    
                return;
            }

            ArrayList<SelectItem> listRegions = (ArrayList<SelectItem>) JAPInfoDBManager.loadRegions();
            
            for(int nInd = 0; nInd < listRegions.size(); nInd++)
            {
                if(strRegionName.equals(listRegions.get(nInd).getLabel()))
                {
                    ((UIInput) component).setValid(false);
                    showMessage(component, context, "Nome de região já existente.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
                    
                    return;
                }
            }
            
            ((UIInput) component).setValid(true);
        }
    }
    
    protected boolean isFilenameValid(String strFilename)
    {
        return !(strFilename.contains("\\") || strFilename.contains("/") || strFilename.contains(":")
               || strFilename.contains("*") || strFilename.contains("?") || strFilename.contains("\"")
               || strFilename.contains("<") || strFilename.contains(">") || strFilename.contains("|"));
    }
}
