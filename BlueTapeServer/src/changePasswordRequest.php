
<?php

$username=$_POST['user'];
$password=$_POST['pass'];
$newPassword=$_POST['newPass'];

function do_post($url, $data)
{
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $url);
  curl_setopt($ch, CURLOPT_POST, 1);
  curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($data));
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

  $response = curl_exec($ch);
  curl_close($ch);
  if ($response == "OK"){
  return $response;
  }
  else {
  return $response;
  }
}
#################
#sends username and encrypted password to server to check for information in database
$url='http://imbrium.seas.wustl.edu:28000/servlet/accountManagement.ChangePasswordServlet';
$data= array(
	'user' => $username,
	'pass' => $password,
	'newPass' => $newPassword);

$httpResponse=do_post($url, $data); #Contains response with error codes and the token if successful
echo $httpResponse;

?>