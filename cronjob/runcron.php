
<?php
include 'db_functions.php';
$db = new DB_Functions_GCM();
$nottype = "default";
echo date('Y-m-d H:i:s')."\r\n";

function sendPushNotification($registration_ids, $message, $pushMessage) {
	$url = GOOGLE_API_URL;
	$fields = array('registration_ids' => $registration_ids, 'data' => $message);
	$headers = array('Authorization:key=' . GOOGLE_API_KEY, 'Content-Type: application/json');
	echo json_encode($fields);
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
	$result = curl_exec($ch);

	if (!$result) {
		die('Curl failed ' . curl_error());
		echo "\r\n".'Notification Failed. Error=' . curl_error();
	} else {echo "\r\n".'Notification Sent';} 
	
	curl_close($ch);

	$db2 = new DB_Functions_GCM();

	$res = $db2 -> storeNotification($registration_ids[0], $pushMessage, json_encode($fields));
        if(!$res) {echo "\r\n".'Failed to write to database';}

	return $result;
}

$pushStatus = '';
$gcmRegIds = array();
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
$sql = "SELECT gcm_regid FROM gcm_users where role='Dependant' and status='paired' and withdraw='No' and complete='No' and gcm_regid is not null";
$result = $conn->query($sql);
if (mysqli_num_rows($result) > 0) {
	 echo mysqli_num_rows($result)."\r\n";
	 while( $row = mysqli_fetch_assoc($result)){
		// echo $row['gcm_regid']."\r\n";
	    array_push($gcmRegIds, $row['gcm_regid']);
	 }
} else {
	echo "\r\n".'0 results';
}

$regIdChunk = array_chunk($gcmRegIds, 1000);
foreach ($regIdChunk as $RegId) {
	$rand_id = rand(1,10);
	// echo "\r\n".'rand_id='.$RegId;
	$sql = "SELECT notification FROM anomalyid where id = '$rand_id' ";
	$result = $conn->query($sql);
	if (mysqli_num_rows($result) > 0) {
		while( $row = mysqli_fetch_assoc($result)){
			$pushMessage = $row["notification"];
		}
		$pushMessage = html_entity_decode($pushMessage);
		$message = array($nottype => $pushMessage);
		// echo "\r\n".$RegId." \nMessage:".$pushMessage;
		$pushStatus = sendPushNotification($RegId, $message, $pushMessage);	
	} else {
		echo "\r\n".'No notification';
	}	
}

?>

