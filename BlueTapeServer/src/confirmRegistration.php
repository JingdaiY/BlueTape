<?
// echo 'email:'.$_GET['email'].'<br>';
// echo 'id:'.$_GET['id'].'<br>';
$connection = mysql_connect('cognitum', 'pmin_bluetape_u', 'hodedob1u3+4p3'); 
mysql_select_db('min_bluetape_db', $connection) or die(mysql_error());
// $q = "SELECT * from preferences;";
// $result = mysql_query($q, $connection);
// while($row = mysql_fetch_array($result)) {
	// var_dump($row); 
	// echo "<br>";
// }
$q = "UPDATE preferences SET is_activated='true',confirmation_id=NULL WHERE email='".$_GET['email'].
		"' AND confirmation_id=".$_GET['id'].";";
$result = mysql_query($q, $connection);
if ($result) header("Location: http://bluetape.seas.wustl.edu/home1.php");
else echo "Fail to confirm email address. Please check with account administrator.";
// $q = "SELECT * from preferences;";
// $result = mysql_query($q, $connection);
// while($row = mysql_fetch_array($result)) {
	// var_dump($row); 
	// echo "<br>";
// }
?>
