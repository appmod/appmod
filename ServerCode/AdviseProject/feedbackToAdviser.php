<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phoneNo=trim($intialArr[0]);
$reply=trim($intialArr[1]);
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
$sql2 = "select adviser FROM gcm_users WHERE phone ='$phoneNo'";
$result1 = $conn->query($sql2);
if (mysqli_num_rows($result1) > 0) {
 while( $row = mysqli_fetch_assoc($result1)){
$adviserfetched=$row['adviser'];
}
}

$sql1 = "select gcm_regid FROM gcm_users WHERE phone = '$adviserfetched'";
$result = $conn->query($sql1);
$registration_ids = array();
if (mysqli_num_rows($result) > 0) {
 while( $row = mysqli_fetch_assoc($result)){
array_push($registration_ids, $row['gcm_regid']);
 
}
}
$nottype= "feedback";
$regIdChunk = array_chunk($registration_ids, 1000);
$pushMessage=$reply.":".$phoneNo;
$pushMessage = html_entity_decode($pushMessage);
$nottype = html_entity_decode($nottype);
$message = array($nottype => $pushMessage);
foreach ($regIdChunk as $RegId) {
$url = GOOGLE_API_URL;

	$fields = array('registration_ids' => $RegId, 'data' => $message, );

	$headers = array('Authorization:key=' . GOOGLE_API_KEY, 'Content-Type: application/json');
	echo json_encode($fields);
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



