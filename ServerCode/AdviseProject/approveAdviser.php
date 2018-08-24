<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phoneNo=trim($intialArr[0]);
$reply=$intialArr[1];
$adviserPhone=trim($intialArr[2]);

if (!empty($phoneNo)) {
 	$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
 	$sql = "insert into approvalstatus(adviser,dependantName,dependantPhone,status, time) values ('$adviserPhone',null,'$phoneNo','$reply',NOW())";
	if ($conn->query($sql) === FALSE) {
	    echo "Error: " . $sql . "<br>" . $conn->error;
	}

 	$updatesql = "update gcm_users set status='paired', adviser='$adviserPhone' WHERE phone ='$phoneNo'";
	if ($conn->query($updatesql) === FALSE) {
	    echo "Error: " . $updatesql . "<br>" . $conn->error;
	}

	$sql1 = "select gcm_regid FROM gcm_users WHERE phone ='$adviserPhone'";
	$result = $conn->query($sql1);

	$registration_ids = array();
	if (mysqli_num_rows($result) > 0) {
	 	while( $row = mysqli_fetch_assoc($result)){
	    	array_push($registration_ids, $row['gcm_regid']); 
	  	}
	} else {
		echo "0 results";
	}
	mysqli_close($conn);

	$nottype= "replyToAdviser";
	$regIdChunk = array_chunk($registration_ids, 1000);
	$pushMessage=$reply.":".$phoneNo;
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
}

?>

