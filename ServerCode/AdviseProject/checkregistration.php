<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once '../../db_files/adv_config.php';
$initialPost = file_get_contents('php://input');
$intialArr = explode(':', $initialPost);
$phoneNo=trim($intialArr[0]);
$dependantPhone=trim($intialArr[1]);

if (!empty($phoneNo)) {
 $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

  $registration_ids = array();

	$flag=true;
	while($flag){
		sleep(120);
		 $selectsql1 = "select gcm_regid FROM gcm_users WHERE phone = '$dependantPhone'";
  $result = $conn->query($selectsql1);
   //$newphone='+65'.$dependantPhone;
	// $sql4 = "select gcm_regid FROM gcm_users WHERE phone = '$newphone'";
  //$result1 = $conn->query($sql4);
  
  //if ((mysqli_num_rows($result) > 0)|| (mysqli_num_rows($result1) > 0 )) {
  if ((mysqli_num_rows($result) > 0)) {
	  $flag=false;
	  $selectsql2 = "select gcm_regid FROM gcm_users WHERE phone = '$phoneNo'";
  	  $result2 = $conn->query($selectsql2);
  
    if (mysqli_num_rows($result2) > 0) {
    while( $row = mysqli_fetch_assoc($result2)){
      array_push($registration_ids, $row['gcm_regid']);
    }
  $nottype= "dependantregistered";
  $regIdChunk = array_chunk($registration_ids, 1000);
  $pushMessage=$dependantPhone;
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
	curl_close($ch);
	return $result;
  }
}
 
}
	}
mysqli_close($conn);
}

?>








