<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phoneNo=trim($intialArr[0]);
$dependantName=trim($intialArr[1]);
$dependantPhone=trim($intialArr[2]);

if (!empty($phoneNo)) {
 $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
  $sql1 = "select gcm_regid FROM gcm_users WHERE phone = '$dependantPhone'";
  $result = $conn->query($sql1);
  //$newphone='+65'.$dependantPhone;
  //$sql5 = "select gcm_regid FROM gcm_users WHERE phone = '$newphone'";
  //$result1 = $conn->query($sql5);
 
  $registration_ids = array();
    //if ((mysqli_num_rows($result) > 0)|| (mysqli_num_rows($result1) > 0 )) {
    if (mysqli_num_rows($result) > 0) {
		//if (mysqli_num_rows($result) > 0){  
	   		$updatesql = "update gcm_users set status='deleted' WHERE phone ='$dependantPhone'";
			if ($conn->query($updatesql) === FALSE) {
	    		echo "Error: " . $updatesql . "<br>" . $conn->error;
			}

    		while( $row = mysqli_fetch_assoc($result)) {
      			array_push($registration_ids, $row['gcm_regid']);
    		}
		//}else{
		//$updatesql = "update gcm_users set status='deleted' WHERE phone ='$newphone'";
		//if ($conn->query($updatesql) === FALSE) {
            //echo "Error: " . $updatesql . "<br>" . $conn->error;
        //}

		//while( $row = mysqli_fetch_assoc($result1)){
		//	array_push($registration_ids, $row['gcm_regid']);
    	//}
		//}
  $nottype= "deletedependant";
  $regIdChunk = array_chunk($registration_ids, 1000);
  $pushMessage=$phoneNo;
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
	if ($result === false){	
		$sql2 = "insert into approvalstatus(adviser,dependantName,dependantPhone,status, time) values ('$phoneNo','$dependantName','$dependantPhone','Curl Failed for Deleting',NOW())";  
	}else{
		$sql2 = "insert into approvalstatus(adviser,dependantName,dependantPhone,status, time) values ('$phoneNo','$dependantName','$dependantPhone','Dependant Deleted',NOW())";
	}
	if ($conn->query($sql2) === FALSE) {
	    echo "Error: " . $sql2 . "<br>" . $conn->error;
	}
	curl_close($ch);
	return $result;
  }
}
else{
	echo "notregistered";
	$sql4 = "insert into approvalstatus(adviser,dependantName,dependantPhone,status, time) values ('$phoneNo','$dependantName','$dependantPhone','notregistered but deleted',NOW())";   
	if ($conn->query($sql4) === FALSE) {
            echo "Error: " . $sql4 . "<br>" . $conn->error;
        }

}
mysqli_close($conn);

}

?>






