<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$flag = file_get_contents('php://input');
$val = 0;
if (!empty($flag)) {
  $message = $flag;
  $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
  //$sql = "SELECT value FROM anomalyid WHERE id=1";
  $sql = "SELECT value FROM anomalyid WHERE notification LIKE '%$message%';";
  $result = $conn->query($sql);
  if (mysqli_num_rows($result) > 0) {
    while( $row = mysqli_fetch_assoc($result)){
       $val = $row["value"];
    }
  }
  $anomalyid=$val;
  $newVal=$val+1;
  $updatesql = "update anomalyid set value=$newVal where notification LIKE '%$message%';";
  if ($conn->query($updatesql) === FALSE) {
	    echo "Error: " . $updatesql . " " . $conn->error;
  } else {echo $anomalyid;}
mysqli_close($conn);
}
?>
