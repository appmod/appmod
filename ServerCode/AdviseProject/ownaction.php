<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phoneNo=trim($intialArr[0]);
$anomalyid=trim($intialArr[1]);
$anomaly=trim($intialArr[2]);
$action=trim($intialArr[3]);
$status=trim($intialArr[4]);
if (!empty($phoneNo)) {
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
$sql2 = "select * FROM approvalstatus WHERE dependantPhone ='$phoneNo'";
$result1 = $conn->query($sql2);
if (mysqli_num_rows($result1) > 0) {
 while( $row = mysqli_fetch_assoc($result1)){
   $adviserfetched=$row['adviser'];
 }
}
$insertsql = "insert into advices (anomalyid,seeker_phone,adviser,anomaly_desc,own_action,own_action_status,own_action_date) values('$anomalyid','$phoneNo','$adviserfetched','$anomaly','$action','$status',NOW())";
if ($conn->query($insertsql) === FALSE) {
	    echo "Error: " . $insertsql . "<br>" . $conn->error;
}

mysqli_close($conn);
}
?>
