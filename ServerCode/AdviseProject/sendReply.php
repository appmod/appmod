<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$anomalyid=trim($intialArr[0]);
$reply=trim($intialArr[1]);
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
$sql = "update advices set advice_given='$reply',advice_given_date=NOW() WHERE anomalyid = '$anomalyid'";
if ($conn->query($sql) === FALSE) {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}

$sql1 = "select * FROM advices WHERE anomalyid = '$anomalyid'";
$result = $conn->query($sql1);
if (mysqli_num_rows($result) > 0) {
 while( $row = mysqli_fetch_assoc($result)){
   $dependant=trim($row['seeker_phone']);
   $advisername= $row['adviser'];
   $anomaly=$row['anomaly_desc'];
  }
}

$sql2 = "select gcm_regid FROM gcm_users WHERE phone = '$dependant'";
$result1 = $conn->query($sql2);
$registration_ids = array();
if (mysqli_num_rows($result1) > 0) {
 while( $row = mysqli_fetch_assoc($result1)){
   array_push($registration_ids, $row['gcm_regid']);
 }
}
$nottype= "adviceReceived";
$regIdChunk = array_chunk($registration_ids, 1000);
$pushMessage=$reply.":".$anomalyid.":".$anomaly;
$pushMessage = html_entity_decode($pushMessage);
$nottype = html_entity_decode($nottype);
$message = array($nottype => $pushMessage);
foreach ($regIdChunk as $RegId) {
    $url = GOOGLE_API_URL;
	$fields = array('registration_ids' => $RegId, 'data' => $message, );
	$headers = array('Authorization:key=' . GOOGLE_API_KEY, 'Content-Type: application/json');
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
	$result = curl_exec($ch);
	if ($result === false)
		die('Curl failed ' . curl_error());
	curl_close($ch);
	return $result;
}

mysqli_close($conn);
?>





