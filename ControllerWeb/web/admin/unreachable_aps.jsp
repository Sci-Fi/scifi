<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
    <head>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8"/>
        <title>Controlador Scifi - Pontos de Acesso Incomunicáveis</title>
        <link href="../css/estilo.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div id="conteudo">
            <table id="table_unreachable">
                <thead>
                    <th>
                        IP
                    </th>
                    <th>
                        MAC
                    </th>
                    <th>
                        Localidade
                    </th>
                    <th>
                        Região
                    </th>
                    <th>
                        Status
                    </th>

                </thead>

                <tbody>
                    <%
                        int unreachable = Integer.parseInt(request.getParameter("params_count"));
                        String IP="", MAC="", loc="", region="";
                        int enabled=0;

                        for(int contador=1;contador <= unreachable; contador++) {
                            IP = request.getParameter("IP"+contador);
                            MAC = request.getParameter("MAC"+contador);
                            loc = request.getParameter("Loc"+contador);
                            enabled = Integer.parseInt(request.getParameter("Enabled"+contador));
                            region = request.getParameter("Region"+contador);

                            %><tr><td><%= IP %></td><td><%= MAC %></td><td><%= loc %></td><td><%= region %></td><td><%= ((enabled==1) ? "Habilitado":"Desabilitado") %></td></tr><%
                        }
                    %>
                </tbody>

            </table>
            <a href="../admin/admin.jsf">Ir para Página Inicial</a>
        </div>
    </body>
</html>