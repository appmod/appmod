<?php
    include_once 'db_functions.php';
    ini_set('display_errors', 'On');
    error_reporting(E_ALL);

    $db = new DB_Functions_GCM();
    $result = $db -> delAll();

    if ($result) {           
	echo 'OK';
    } else {
	echo 'Fail';
    }

?>
