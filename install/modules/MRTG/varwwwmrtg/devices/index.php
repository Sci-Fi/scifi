<?php
    //ESTE CODIGO LISTA E ORDENA OS DIRETORIOS DO MRTG
    $path = "./";

    $diretorios = dir($path);
    $lista = array();

    while ($diretorio = $diretorios->read()) {
        if (is_dir($diretorio) && $diretorio != "." && $diretorio != ".." && $diretorio != "figuras" && $diretorio != "css" && $diretorio != "javascript" && $diretorio != "classes"  && $diretorio != "devices") {
            array_push($lista, $diretorio);
        }
    }

    sort($lista);
?>

<table id="tabelaAP">
    <thead>
        <th>APs</th>
    </thead>
    <tbody>
        <?php
            function returnString($valor) {
                $ap = substr(strtoupper($valor), 0, 2);

                if ($ap == "AP") {
                    return $ap . " " . substr(strtoupper($valor), 2);
                } else {
                    return ucfirst($valor);
                }
            }

            foreach ($lista as $valor) {
        ?>
            <tr>
                <td><a class="aps" href="index.php?page=devices&ap=<?php echo $valor ?>"><?php echo returnString($valor); ?></a></td>
            </tr>
        <?php
            }
        ?>
    </tbody>
</table>