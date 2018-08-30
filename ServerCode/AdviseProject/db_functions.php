<?php
require_once '../../db_files/adv_config.php';

class DB_Functions_GCM {

	//put your code here
	// constructor
	function __construct() {
		// connecting to database
		$GLOBALS['mysqli_connection'] = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE) or die("Mysqli Error " . mysqli_error($GLOBALS['mysqli_connection']));

	}

	// destructor
	function __destruct() {

	}

	public function connectDefaultDatabase() {

		$GLOBALS['mysqli_connection'] = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE) or die("Mysqli Error " . mysqli_error($GLOBALS['mysqli_connection']));
		return $GLOBALS['mysqli_connection'];
	}

	public function selectDatabase($db) {
		mysqli_select_db($GLOBALS['mysqli_connection'], $db);
	}

	public function closeDatabase() {
		mysqli_close($GLOBALS['mysqli_connection']);
	}

	public function connectNewDatabase($host, $user, $password, $dbname = "") {
		closeDatabase();

		if ($dbname != "" && $dbname != null) {
			$GLOBALS['mysqli_connection'] = mysqli_connect($host, $user, $password, $dbname) or die("Mysqli Error " . mysqli_error($GLOBALS['mysqli_connection']));
		} else {$GLOBALS['mysqli_connection'] = mysqli_connect($host, $user, $password) or die("Mysqli Error " . mysqli_error($GLOBALS['mysqli_connection']));
		}

		return $GLOBALS['mysqli_connection'];
	}

	/**
	 * Storing new user
	 * returns user details
	 */

	public function getUserById($id) {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "SELECT * FROM gcm_users WHERE id = '$id' LIMIT 1");
		return $result;
	}

	/**
	 * Getting all users
	 */
	public function getAllUsers() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "select * FROM gcm_users where role='Dependant' and status='paired' and withdraw='No' ");
		return $result;
	}

	public function getAllAdvisors() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "SELECT * FROM gcm_users where role = 'Adviser' and phone in (select adviser from gcm_users where role='Dependant' and status='paired' and withdraw='No');");
		return $result;
	}


	/**
	 * Check user exists or not
	 */
	public function checkUserById($id) {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "SELECT gcm_regid from gcm_users WHERE gcm_regid = '$id'");
		$no_of_rows = mysqli_num_rows($result);
		if ($no_of_rows > 0) {
			return true;
		} else {
			return false;
		}
	}

	public function checkUserByPhone($phone) {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "SELECT gcm_regid from gcm_users WHERE phone = '$phone'");
		$no_of_rows = mysqli_num_rows($result);
		if ($no_of_rows > 0) {
			return true;
		} else {
			return false;
		}
	}

	public function deleteUserById($id) {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "DELETE FROM gcm_users WHERE gcm_regid = '$id'");
		if ($result) {
			return true;
		} else {
			return false;
		}

	}

	public function submit_consent($id){
		// date_default_timezone_set('Asia/Singapore');
		// $submit_date = date("Y-m-d h:i:s");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "INSERT INTO consent(id, submit_date) VALUES('$id', NOW())");
		if ($result) {
			return true;
		} else {
			echo "You have already submitted the consent form with id: ".$id;
			return false;
		}
	}

	public function deleteUserByPhone($phone) {
                $result = mysqli_query($GLOBALS['mysqli_connection'], "DELETE FROM gcm_users WHERE phone = '$phone'");
                if ($result) {
                        return true;
                } else {
                        return false;
                }

        }

	public function updateUserCompleteById($id) {
                $result = mysqli_query($GLOBALS['mysqli_connection'], "UPDATE gcm_users set complete='Yes' WHERE gcm_regid = '$id'");
                if ($result) {
                        return true;
                } else {
                        return false;
                }

        }
	
	//for user study only
	public function updateUser($gcm_regid, $instanceId, $phone){
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update gcm_users set gcm_instance_id = '$instanceId', gcm_regid = '$gcm_regid', login_at = NOW() where phone = '$phone'");

		echo '*****result='.$result;
		if($result){
			return true;
		}
		else{
			return false;
		}
	}

	public function storeUser($gcm_regid, $instanceId,$phone,$role) {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "INSERT INTO gcm_users(phone, role,gcm_instance_id, gcm_regid, login_at) VALUES('$phone','$role', '$instanceId', '$gcm_regid', NOW())");
		//echo "*****result=".$result;	
		// check for successful store
		if ($result) {
			// get user details
			$id = mysqli_insert_id($GLOBALS['mysqli_connection']);
			// last inserted id
			$result = mysqli_query($GLOBALS['mysqli_connection'], "SELECT * FROM gcm_users WHERE id='$id'") or die("Error " . mysqli_error($GLOBALS['mysqli_connection']));
			// return user details
			if (mysqli_num_rows($result) > 0) {
				$hashcode = md5("smuappmod".$id.$phone.$gcm_regid."2018");
				$result = mysqli_query($GLOBALS['mysqli_connection'], "UPDATE gcm_users set hashcode='$hashcode' where id='$id'");
				return $result;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public function getAllNotifications() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "select * FROM anomalyid where notification not like '%user survey%' ");
		return $result;
	}

	public function getMsgOnly() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "select * FROM anomalyid where notification like '[msg]%'");
		return $result;
	}

	public function storeNotification($registration_ids, $message, $fields) {
		// insert user into database
		$result = mysqli_query($GLOBALS['mysqli_connection'], "INSERT INTO notificationlog(regid, notification,fields,senddate) VALUES('$registration_ids','$message', '$fields', NOW())");
		if ($result) {
			return true;
		} else {
			return false;
		}
	}

    	public function getAll() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "select * FROM gcm_users");
		return $result;
	}

    	public function showComplete() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "select seeker_phone, adviser, count(*) as freq from (SELECT distinct seeker_phone, adviser, anomaly_desc FROM advices) s1 group by seeker_phone, adviser ");
		return $result;
	}

	public function delAll() {
		$result = mysqli_query($GLOBALS['mysqli_connection'], "Truncate table advices");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "Truncate table approvalstatus");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "Truncate table gcm_users");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "Truncate table notificationlog");

		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=1000 where id=1");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=2000 where id=2");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=3000 where id=3");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=4000 where id=4");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=5000 where id=5");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=6000 where id=6");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=7000 where id=7");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=8000 where id=8");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=9000 where id=9");
		$result = mysqli_query($GLOBALS['mysqli_connection'], "update anomalyid set value=10000 where id=10");
		if ($result) {
			return true;
		} else {
			return false;
		}
	}
}
?>


