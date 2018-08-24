<html>
    <head>
        <title>Push Notification Admin Panel (Advisor - 18 to 39 yrs old)</title>
        <style type="text/css">
			html {
				height: 100%;
				font-size: 62.5%
			}
			body {
				height: 100%;
				background-color: #FFFFFF;
				font: 1.4em Verdana, Arial, Helvetica, sans-serif;
			}

			/* ==================== Form style sheet ==================== */

			/*div { align-content: center; padding-bottom: 30px; }*/

			fieldset {
				display: inline;
				align-content: center;
				border: 1px solid #095D92;
				width: 80%
			}
			legend {
				font-size: 1.1em;
				background-color: #707070;
				color: #FFFFFF;
				font-weight: bold;
			}

			input.submit-button {
				font: 1.4em Georgia, "Times New Roman", Times, serif;
				letter-spacing: 1px;
				display: block;
				width: 25em;
				height: 2em;
				background-color: #d57079;
			}

			/* ==================== Form style sheet END ==================== */

</style>
</head>
<body>
 <br><br>
    <?php
     ini_set('display_errors', 'On');
     error_reporting(E_ALL);
     require_once 'db_functions.php';
     function get_users() {
		$db = new DB_Functions_GCM();
		$result = $db -> getAllAdvisors();
		$users = array();
		if ($result != false) {			
            if (mysqli_num_rows($result) > 0) {
			  $i=0;
              while( $row = mysqli_fetch_assoc($result)) {
                array_push($users, $row['id'].':'.$row['phone']);
				$i++;
              }
			}
		}
		$str='';
		while(list($k,$v)=each($users)) {
				$arr =explode(':', $v); 
				$id =$arr[0]; 
				$phone = $arr[1];
				$str.='<br><input type="checkbox" value="'.$id.'" name="user[]"/>'.$phone.'<br>';
			}
        return $str;
		}

		function get_notifications() {
		$db = new DB_Functions_GCM();
		$result = $db -> getMsgOnly();
		$messages = array();
		if ($result != false) {			
            if (mysqli_num_rows($result) > 0) {
			  $i=0;
              while( $row = mysqli_fetch_assoc($result)){
                array_push($messages, $row['id'].':'.$row['notification']);
				$i++;
              }
			}
		}
		$str='';
		while(list($k,$v)=each($messages)) {
				$arr =explode(':', $v); 
				$id =$arr[0]; 
				$notification = $arr[1];
				$str.='<br><input type="radio" value="'.$notification.'" name="message"/>'.$notification.'<br>';
			}
        return $str;
		}
    ?>
      
 <div><fieldset>

    <h3>Push Notification Panel <font color="red">(Advisor - 18 to 39 yrs old)</font></h3></fieldset></div><br></br>

    <form method = 'POST' action = 'gcm_main.php' enctype="multipart/form-data">
	
	<div><fieldset><legend>Select id(s)</legend>
    <?php echo get_users(); ?>
	<br>
       </fieldset>
	</div><br>
   
    <div><fieldset><legend>Select Notification</legend>
	<br>
		<?php echo get_notifications(); ?>
       
    <br></fieldset>
	</div><br>
    <div>
            <input type = "submit" value = "Send Notification">
    </div>
        
    </form>
    </body>
</html>


