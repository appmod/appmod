
<?php
include 'db_functions.php';
$db = new DB_Functions_GCM();
include 'commonutils.php';

//////////////Do some validation First//////////////
$error = 0;
if (isset($_POST['message'])) {
	$pushMessage = $_POST['message'];
	if ($pushMessage == "" || $pushMessage == null) {echo "<br>Error: message can not be empty<br>";
		$error++;
	}
} else {echo "<br>Nothing received <br>";
	$error++;
}

$nottype = "default";

//////exit if error/////
if ($error != 0) {echo "<br><a href=\"" . constant("PWD") . "\">Go Back</a><br>";
	exit ;
}

function sendPushNotification($registration_ids, $message, $pushMessage) {

	$url = GOOGLE_API_URL;
	$fields = array('registration_ids' => [$registration_ids], 'data' => $message);
	$headers = array('Authorization:key=' . GOOGLE_API_KEY, 'Content-Type: application/json');
	//echo "<br>json=".json_encode($fields);
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	$result = curl_exec($ch);

	if ($result === FALSE) {
		die('Curl failed ' . curl_error());
		echo '<br>Notification Failed. Error=' . curl_error();
	} else {
		echo '<br>Notification Sent<br>';
		$pos2 = strpos($pushMessage, 'Thank you');
		$pos3 = strpos($pushMessage, 'user survey');
		if ($pos2 !== false || $pos3 !== false) {
			$db3 = new DB_Functions_GCM();
			$res1 = $db3 -> updateUserCompleteById($registration_ids);
	        	if(!$res1) {echo 'Failed to write to database';}
		}
	} 
	
	curl_close($ch);

	$db2 = new DB_Functions_GCM();
	$res = $db2 -> storeNotification($registration_ids, $pushMessage, json_encode($fields));
        if(!$res) {echo 'Failed to write to database';}

	return $result;
}

$pushStatus = '';
$gcmRegIdsHashcodes = array();

if(isset($_POST['user'])) {
    foreach($_POST['user'] as $id) {
        $query = "SELECT gcm_regid, hashcode FROM gcm_users where id='$id'";
		if ($query_run = mysqli_query($GLOBALS['mysqli_connection'], $query)) {
			while ($query_row = mysqli_fetch_assoc($query_run)) {
				array_push($gcmRegIdsHashcodes, $query_row['gcm_regid'].'^^^'.$query_row['hashcode']);
			}
		}
	}
} else {echo "<br>Please check at least one checkbox.<br>";
	$error++;
}

if (isset($gcmRegIdsHashcodes) && isset($pushMessage)) {
	$pushMessage = html_entity_decode($pushMessage);
	//$message = array($nottype => $pushMessage);
	//$regIdChunk = array_chunk($gcmRegIdsHashcodes, 1000);
	foreach ($gcmRegIdsHashcodes as $RegIdHashcode) {
		$intialArr = explode('^^^', $RegIdHashcode);
		$RegId=$intialArr[0];
		$hashcode=$intialArr[1];
		$pos = strpos($pushMessage,'xxxxx');
		if ($pos !== false) {
			$tmp = explode('xxxxx', $pushMessage);
			$m0 = $tmp[0];
			$m1 = $tmp[1];
			$pushMessage = $m0.$hashcode.$m1;
		}

		$message = array($nottype => $pushMessage);
		$pushStatus = sendPushNotification($RegId, $message, $pushMessage);
	}
//redirect(PWD); //Comment this if you do not want to be redirected to previous page
} else {
	echo "Unknown error occured, contact your Push Notification Service Provider";
	exit ;
}
?>


