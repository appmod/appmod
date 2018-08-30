<?php

    $json = array();

    if (isset($_POST["regId"])) { 
        $instanceId = '';
        $gcm_regid = $_POST["regId"];
        if(isset($_POST["instanceId"])){$instanceId=$_POST["instanceId"];}
        if(isset($_POST["phone"])){$phone=trim($_POST["phone"]);}
        //$phone="+".$phone;
        if(isset($_POST["role"])){$role=$_POST["role"];}		
        include_once 'db_functions.php';
        $db = new DB_Functions_GCM();
        //echo '<br>'.$gcm_regid;
        //echo '<br>'.$instanceId;
        //echo '<br>'.$phone;
        //echo '<br>'.$role;
        
        //this code is for user study only
        if($db -> checkUserByPhone($phone) == true){
            $res = $db -> updateUser($gcm_regid,$instanceId,$phone);
            if($res){
                echo 'Success!';
            }else{
                echo 'Failed; cannot update';
            }
        }
        else{
            echo 'Failed';
        }
    }
    else {
            echo 'Failed! No regId';
    }
//         if ($db -> checkUserById($gcm_regid) == false && $db -> checkUserByPhone($phone) == false && $gcm_regid != "" && $gcm_regid != null) {
// 		        $res = $db -> storeUser($gcm_regid,$instanceId,$phone,$role);
//                 if(!$res){
//                     echo 'Failed! User already exists.';
//                 }
// 		        else {
//                     echo 'Success!';
//                 }
//         } 
//         else if ($db -> checkUserById($gcm_regid) == true && $db -> checkUserByPhone($phone) == true &&  $gcm_regid != "" && $gcm_regid != null) {
//  		    echo 'Success!';
// 	    } else {
// /*
//             if ($db -> checkUserById($gcm_regid) == true && $gcm_regid != "" && $gcm_regid != null) {
//                     $res = $db -> deleteUserById($gcm_regid);
//                     $res = $db -> storeUser($gcm_regid,$instanceId,$phone,$role);
//                         if(!$res){echo '<br>Failed to write to database';}
//             }
// */
// 	        echo 'Failed! User already exists';
//         }
//     } 
    //    else {
    //         echo 'Failed! No regId';
    //    }
?>



