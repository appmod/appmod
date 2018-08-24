<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phone=trim($intialArr[0]);
$action=trim($intialArr[1]);

if (!empty($phone)) {
  $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
  $updatesql = "update gcm_users set withdraw='Yes' WHERE phone = '$phone'";
  if ($conn->query($updatesql) === FALSE) {
	echo "Error: " . $updatesql . "<br>" . $conn->error;
  }

mysqli_close($conn);
}
?>
