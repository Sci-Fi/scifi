/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import data.JAPInfo;
import database.JAPInfoDBManager;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Classe que cont�m m�todos de valida��o dos valores dos parâmetros do JAPInfo inseridos via interface de usu�rio.
 * @author controlador
 */
public class JAPInfoValidator extends JValidator {

    /**
     * M�todo que verifica se um determinado AP j� existe na lista de APs.
     * @param strMAC MAC do AP.
     * @return Retorna true se o AP est� na lista ou false, caso contr�rio.
     */
    public boolean MACExists(String strMAC) {
        ArrayList<JAPInfo> listAP = JAPInfoDBManager.getAPListFromDB();

        for (int nInd = 0; nInd < listAP.size(); nInd++) {
            if (listAP.get(nInd).getMAC().equals(strMAC)) {
                return true;
            }
        }
        return false;
    }

    /**
     * M�todo que verifica se um determinado IP j� est� sendo utilizado por um dos APs controlados.
     * @param strIP IP a ser verificado.
     * @return Retorna true se o IP existe ou false, caso contr�rio.
     */
    public boolean IPExists(String strIP) {
        ArrayList<JAPInfo> listAP = JAPInfoDBManager.getAPListFromDB();

        for (int nInd = 0; nInd < listAP.size(); nInd++) {
            if (listAP.get(nInd).getIP().equals(strIP)) {
                return true;
            }
        }
        return false;
    }

    /**
     * M�todo que valida o MAC. Se o MAC estiver fora do padr�o XX:XX:XX:XX:XX:XX ou os valores estiverem fora do limite, o MAC ser� dado como inv�lido.
     * Caso o MAC seja inv�lido, mensagens aparecer�o na tela indicando este fato e a opera��o de inser��o de um AP n�o poder� ser realizada. 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkMAC(FacesContext context, UIComponent component, Object value) {
        if (!isEmpty(context, component, value)) {
            String strMAC = (String) value;

            Pattern patternMAC = Pattern.compile("((?:(\\d{1,2}|[a-fA-F]{1,2}){2})(?::|-*)){6}");
            Matcher matcher = patternMAC.matcher(strMAC);

            if (!matcher.matches()) {
                ((UIInput) component).setValid(false);
                showMessage(component, context, "MAC inv�lido.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
            } else {
                if (MACExists(strMAC)) {
                    ((UIInput) component).setValid(false);
                    showMessage(component, context, "MAC existente.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
                } else {
                    ((UIInput) component).setValid(true);
                }
            }
        }
    }

    /**
     * M�todo que valida o IP. Se o IP estiver fora do padr�o XXX.XXX.XXX.XXX, ou os valores estiverem fora do limite, ou o IP for repetido, ele ser� dado como inv�lido.
     * Caso o IP seja inv�lido, mensagens aparecer�o na tela indicando este fato e a opera��o de inser��o de um AP n�o poder� ser realizada. 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkIP(FacesContext context, UIComponent component, Object value) {
        if (!isEmpty(context, component, value)) {
            String strIP = (String) value;

            if (!isIPValid(strIP)) {
                ((UIInput) component).setValid(false);

                showMessage(component, context, "IP inv�lido", SHORT_MESSAGE, FILL_DATA_MESSAGE);
            } else {
                if (IPExists(strIP)) {
                    ((UIInput) component).setValid(false);
                    showMessage(component, context, "IP existente.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
                } else {
                    ((UIInput) component).setValid(true);
                }
            }
        }
    }

    /**
     * M�todo que valida a lista de poss�veis pot�ncias de transmiss�o do AP.
     * Se a lista estiver fora do padr�o {a,b,c,d,...,z)}, ou se os valores estiverem duplicados ou fora da ordem crescente, a lista ser� dada como inv�lida.
     * Caso a lista seja inv�lida, mensagens aparecer�o na tela indicando este fato e as opera��es de inser��o e altera��o de um AP n�o poder�o ser realizadas. 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkListTxPower(FacesContext context, UIComponent component, Object value) {
        if (!isEmpty(context, component, value)) {
            String strList = (String) value;

            Pattern patternIP = Pattern.compile("^\\{([0-9]{1,2}[,])*[0-9]{1,2}\\}$");
            Matcher matcher = patternIP.matcher(strList);

            if (!matcher.matches()) {
                ((UIInput) component).setValid(false);

                showMessage(component, context, "Lista de pot�ncias inv�lida", SHORT_MESSAGE, FILL_DATA_MESSAGE);
            } else {
                //checando se a lista est� em ordem crescente e sem valores duplicados.
                strList = strList.substring(1, strList.length() - 1);
                String[] arrayTxPower = strList.split(",");

                for (int nInd = 0; nInd < arrayTxPower.length - 1; nInd++) {
                    if ((Integer.valueOf(arrayTxPower[nInd]) >= Integer.valueOf(arrayTxPower[nInd + 1]))) {
                        ((UIInput) component).setValid(false);
                        FacesMessage msg = new FacesMessage("Lista com valores duplicados ou fora de ordem");
                        context.addMessage(component.getClientId(context), msg);
                        return;
                    }
                }

                ((UIInput) component).setValid(true);
            }
        }
    }

    /**
     * M�todo que analisa se o valor do limiar de sobrecarga � superior ao de carga baixa.
     * 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkOverloadThreshold(FacesContext context, UIComponent component, Object value) {
        if (isUnsignedInteger(context, component, value)) {
            Integer nOverloadThreshold = (Integer) value;

            UIViewRoot root = context.getViewRoot();

            UIComponent componentUnderload = findComponent(root, "APUnderloadThreshold");

            Integer nUnderloadThreshold = (Integer) (((UIInput) componentUnderload).getValue());

            if (nUnderloadThreshold < nOverloadThreshold) {
                ((UIInput) component).setValid(true);
            } else {
                ((UIInput) component).setValid(false);

                showMessage(component, context, "O valor deve ser maior e diferente do que o limite de carga baixa.", SHORT_MESSAGE, FILL_DATA_MESSAGE);
            }
        }
    }

    /**
     * M�todo que valida a latitude.
     * 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkLatitude(FacesContext context, UIComponent component, Object value) {

        Double dblLat = (Double) value;

        if (dblLat == 0) {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Latitude inválida", SHORT_MESSAGE, FILL_DATA_MESSAGE);
        } else {
            ((UIInput) component).setValid(true);
        }
    }

    /**
     * M�todo que valida a longitude.
     * 
     * @param context Contexto do Faces.
     * @param component Componente que est� sendo validado.
     * @param value Valor do componente que est� sendo validado.
     */
    public void checkLongitude(FacesContext context, UIComponent component, Object value) {
        
        Double dblLong = (Double) value;

        if (dblLong == 0) {
            ((UIInput) component).setValid(false);

            showMessage(component, context, "Longitude inválida", SHORT_MESSAGE, FILL_DATA_MESSAGE);
        } else {
            ((UIInput) component).setValid(true);
        }
    }
}
