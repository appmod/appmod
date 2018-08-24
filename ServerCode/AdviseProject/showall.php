<html>
<head>
<style>
        table {
                border-collapse: collapse;
        }
        table, th, td {
                border: 1px solid black;
                text-align: center;
        }
</style>
</head>
</html>


<?php

    include_once 'db_functions.php';
    ini_set('display_errors', 'On');
    error_reporting(E_ALL);

    $db = new DB_Functions_GCM();
    $result = $db -> getAll();

    if ($result != false) {           
        if (mysqli_num_rows($result) > 0) {
            echo "<table style=width:50%><tr><th>ID</th><th>R</th><th>P</th><th>G</th><th>W</th><th>H</th><th>L</th><th>C</th></tr>";
            while( $row = mysqli_fetch_assoc($result)){
                $id = $row['id'];
		$role = substr($row['role'],0,1);
                $phone = $row['phone'];
		$gcmid = substr($row['gcm_regid'],0,5);
                $withdraw = substr($row['withdraw'],0,1);
		$hash = substr($row['hashcode'],0,5);
                $login = $row['login_at'];
                $complete = substr($row['complete'],0,1);
                
                echo '<tr><td>'.$id.'</td><td>'.$role.'</td><td>'.$phone.'</td><td>'.$gcmid.'</td><td>'.$withdraw.'</td><td>'.$hash.'</td><td>'.$login.'</td><td>'.$complete.'</td></tr>';
	   }
        }
        echo '</table>';
    }
?>


