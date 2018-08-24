<?php
$json = array();
if (isset($_POST["regId"])) {
	$gcm_regid = $_POST["regId"];
	include_once 'db_functions.php';
	$db = new DB_Functions_GCM();
	if ($db -> checkUserById($gcm_regid) == true) {
		$res = $db -> deleteUserById($gcm_regid);
	}
} else {
	// user details missing
}
?>

