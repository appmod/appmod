<!DOCTYPE html>
<html>
<head>
<title>SMU-IRB: Participant Information Sheet and Informed Consent Form (Online) </title>
</head>
<body>
<?php 
    include 'db_functions.php';
    $db = new DB_Functions_GCM();

    echo $db->submit_consent($_POST['id']);
?>

<h2>Thank you for the participation!</h2>

<p>Please refer to the <a href="../AppMod/AppMod-UserGuide.pdf">user guide</a> of the AppMode application.</p>

</body>