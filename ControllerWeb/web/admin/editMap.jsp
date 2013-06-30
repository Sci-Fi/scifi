<%@page import="java.io.PrintWriter"%>
<%@page import="beans.JAPListBean"%>
<%@page import="database.JAPInfoDBManager"%>
<%@page import="beans.JControllerCommanderBean"%>
<%@page import="database.JPropertyDBManager"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="data.JAPInfo"%>
<%@page import="data.JProperty"%>
<%@page import="javax.faces.model.SelectItem"%>

<%@page contentType="application/json" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <body>    
        <%
            //ESTE PÁGINA É UMA PÁGINA DE CONTROLE. ELA RECEBE A REQUISIÇÃO DA PÁGINA E A ENCAMINHA PARA A PERSISTÊNCIA

            PrintWriter out2 = response.getWriter();
            int type = Integer.parseInt(request.getParameter("type"));
            double lat = 0;
            double lng = 0;
            int zoom = 0, nRegion = 0, subtype = 0;
            boolean result = true;
            String MAC = "", url_mrtg = "";

            switch (type) {

                case 0:
                        //Atualiza propriedades do mapa
                        lat = Double.parseDouble(request.getParameter("lat"));
                        lng = Double.parseDouble(request.getParameter("lng"));
                        zoom = Integer.parseInt(request.getParameter("zoom"));

                        result = JAPInfoDBManager.updateDefaultMap(lat, lng, zoom);
                        out2.println(result);

                        out2.flush();
                        out2.close();

                    break;

                case 1:
                        //Esta opção atualiza, à medida que o usuario arrasta o marcador de uma posição a outra, no mapa.
                        lat = Double.parseDouble(request.getParameter("lat"));
                        lng = Double.parseDouble(request.getParameter("lng"));
                        MAC = request.getParameter("MAC").toString();

                        result = JAPInfoDBManager.updateCoordinates(lat, lng, MAC);
                        out2.println(result);

                        out2.flush();
                        out2.close();

                    break;

                case 2:
                        //Busca o AP por MAC ou IP e retorna para o mapa
                        String MACorIP = request.getParameter("MACorIP").toString();
                        
                        ArrayList<JAPInfo> check = JAPInfoDBManager.checkExistenceAP(MACorIP);
                        out2.println(new Gson().toJson(check));

                        out2.flush();
                        out2.close();

                    break;

                case 3:
                        //Remove ponto de acesso
                        String ID = request.getParameter("id").toString();

                        boolean delete_ap = new JAPListBean().removeAP(ID);
                        out2.println((delete_ap) ? 1:0);

                        out2.flush();
                        out2.close();

                    break;

                case 4:
                        //Habilita/Desabilita AP
                        String MAC_ID = request.getParameter("id").toString();
                        int ENABLED_PARAM = Integer.parseInt(request.getParameter("enabled"));
                        nRegion = Integer.parseInt(request.getParameter("region"));

                        int atualiza = new JAPListBean().enableAP(MAC_ID, ENABLED_PARAM, nRegion);
                        out2.println(atualiza);
                        
                        out2.flush();
                        out2.close();

                    break;

                case 5:
                    //Retorna uma lista de todos os APs
                    ArrayList<JAPInfo> array = JAPInfoDBManager.getAPListFromDB();
                    out2.println(new Gson().toJson(array));

                    out2.flush();
                    out2.close();

                    break;

                case 6:
                    //Retorna as propriedas do mapa
                    String LATITUDE = "", LONGITUDE = "";
                    int ZOOM = 0;
                    
                    LATITUDE = JPropertyDBManager.getValueProperty("Latitude");
                    LONGITUDE = JPropertyDBManager.getValueProperty("Longitude");
                    ZOOM = Integer.parseInt(JPropertyDBManager.getValueProperty("Zoom"));

                    out2.println("{\"latitude\":" + LATITUDE + ",\"longitude\":" + LONGITUDE + ",\"zoom\":" + ZOOM + "}");

                    out2.flush();
                    out2.close();
                    break;

                case 7:
                    //Retorna se o AP está comunicante ou não
                    MAC = request.getParameter("id").toString();
                    int reachable = JAPInfoDBManager.getReachable(MAC);
                    out2.println(reachable);

                    out2.flush();
                    out2.close();

                    break;

                case 8:
                    //Reinicia o ponto de acesso
                    MAC = request.getParameter("id").toString();
                    nRegion = Integer.parseInt(request.getParameter("region"));

                    int reboot = new JAPListBean().rebootAP(MAC, nRegion);
                    out2.println(reboot);

                    out2.flush();
                    out2.close();

                    break;

                case 9:
                    //Retorna as regiões
                    ArrayList<SelectItem> arrayRegions = JAPInfoDBManager.loadRegions();
                    out2.println(new Gson().toJson(arrayRegions));

                    out2.flush();
                    out2.close();

                    break;

                case 10:
                    //Redireciona o site para a página do MRTG no SCIFI
                    url_mrtg = JPropertyDBManager.getValueProperty("MRTG");
                    response.sendRedirect(url_mrtg);

                    break;

                case 11:
                    //Redireciona o site para a página do MRTG, na pagina de dispositivos, no SCIFI, em algum AP especifico
                    url_mrtg = JPropertyDBManager.getValueProperty("MRTG");
                    
                    out2.println(url_mrtg);

                    out2.flush();
                    out2.close();
                    //String AP_MRTG = request.getParameter("ap_mrtg").toString();                    
                    //response.sendRedirect(url_mrtg + "index.php?page=devices&ap=" + AP_MRTG);

                    break;
            }
        %>
    </body>    
</html>

