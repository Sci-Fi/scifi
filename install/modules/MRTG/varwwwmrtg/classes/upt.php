<h3>Gr&aacute;ficos de Tempo de UPTIME dos dispositivos</h3>

<form name="input" action="index.php" method="get">
<input type="radio" name="periodo" value="day" checked> Di&aacuterio
<input type="radio" name="periodo" value="week"> Semanal
<input type="radio" name="periodo" value="month"> Mensal
<input type="radio" name="periodo" value="year"> Anual  <br>
<select name="conjunto">
<option value="">Todos</option>
<?php include_once "classes/menu_loc.html";
?>
</select>
<input type="radio" name="page" value="classes" checked > Exibir

<input type="radio" name="classe" value="upt" checked> Uptime

<input type="submit" value="Mostrar">
</form>

<ul id="listMRTG">
<?php
    $dir = "./";
    $type_class = "upt";
    $time_graph_class = $periodo;
    $folders = dir($dir);
    $sort_root = array();
    $path_file = "";

        $folders = dir($dir);

        while($folder = ($folders -> read())) {
                array_push($sort_root, $folder);
        }

        sort($sort_root);

        foreach($sort_root as $folder) {

                if (is_dir($dir.$folder) && $folder != "." && $folder != ".." && $folder != "figuras" && $folder != "css" && $folder != "javascript" && $folder != "classes" && $folder != "devices") {
                        $subdir = dir($dir.$folder);

                        $list = array();

                        while($file = ($subdir->read())) {
                                $path_file = $dir.$folder."/".$file;

                                if(is_file($path_file) && $file != "." && $file != "..") {
                                        //array_push($list, $file);
                                         $file_extension = pathinfo($path_file, PATHINFO_EXTENSION);

                                         if(substr_count($file, $type_class) > 0 && substr_count($file, $time_graph_class) > 0 && ($file_extension == "png" || $file_extension == "jpg" || $file_extension == "jpeg")) {
                                                array_push($list, $file);
                                         }

                                         if(substr_count($file, $type_class) > 0 && $file_extension == "html") {
                                                array_push($list, $file);
                                         }
                                }
                        }

                        sort($list);
                        $increase_vector_control = 0;

                        while($increase_vector_control < count($list)) {

                                $img = $folder."/".$list[$increase_vector_control];
                                $link = $folder."/".$list[$increase_vector_control+1];


                                if (($conjunto == "") || (file_exists ($folder."/".$conjunto))) { ?>
                                        <li>
                                         <?php echo $list[$increase_vector_control+1]; ?>
                                        <br/>

                                        <a href="<?php echo $link; ?>"><img src="<?php echo $img; ?>"/></a>

                                        </li>

                                <?php
                                }

                                $increase_vector_control = $increase_vector_control + 2; //Ja foi usado a imagem e o link desta imagem, o proximo loop sera da proxima imagem e seu link
                        }
                }
        }

   /*
        while($folder = ($folders->read())) {
                if (is_dir($dir.$folder) && $folder != "." && $folder != ".." && $folder != "figuras" && $folder != "css" && $folder != "javascript" && $folder != "classes" && $folder != "devices") {
             $subdir = dir($dir.$folder);
//              echo $dir.$folder;
             $link_class = "";
             while (($file = ($subdir->read())) && ($found == 0)) {

                $path_file = $dir.$folder."/".$file;
//              echo $path_file."<br/>";
                if (is_file($path_file) && $file != "." && $file != "..") {

                    $file_extension = pathinfo($path_file, PATHINFO_EXTENSION);

                    if(substr_count($file, $type_class) > 0 && substr_count($file, $time_graph_class) > 0 && ($file_extension == "png" || $file_extension == "jpg" || $file_extension == "jpeg")) {
                        $img = $path_file;
                    }

                    if(substr_count($file, $type_class) > 0 && $file_extension == "html" && $img != "") {
                         $link_class = $file;
//                         echo $folder."/".$link_class." ---------- ".$img;
                         ?>
                             <!--<li><a href="index.php?page=statistics&ap=<?php //echo $folder ?>&interface=<?php //echo $link_class ?>"><img src="<?php //echo $img; ?>"/></a></li>-->
                             <li><a href="<?php echo $path_file; ?>"><img src="<?php echo $img; ?>"/></a></li>

                        <?php

                        $found = 1;
                        $img = "";
                        $path_file = "";
                        $link_class = "";

                    }
                } else {
//                      $img = "";
//                      $path_file = "";
//                      $link_class = "";
                }
             }
//    $found = FALSE;

             closedir($subdir);
         }
    }*/
?>
</ul>
