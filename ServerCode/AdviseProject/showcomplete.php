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
    $result = $db -> showComplete();

    if ($result != false) {           
        if (mysqli_num_rows($result) > 0) {
	    echo "<table style=width:30% ><tr><th>D</th><th>A</th><th>Count</th></tr>";
            while( $row = mysqli_fetch_assoc($result)){
                $dependantphone = $row['seeker_phone'];
                $advisorphone = $row['adviser'];
                $frequency = $row['freq'];
                echo '<tr><td>'.$dependantphone.'</td><td>'.$advisorphone.'</td><td>'.$frequency.'</td></tr>';
            }
	    echo '</table>';
        }
    }

?>

