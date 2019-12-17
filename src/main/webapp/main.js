var errorMessage="Wrong Credentials Try Again";
var postUrl='http://localhost:8080/HelloGradleWebApp/rest/service/login';
var getUrl='http://localhost:8080/HelloGradleWebApp/rest/service/welcome';
function loadDoc() {
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.open('POST',postUrl, true);
	xmlHttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	var name = document.getElementById("name").value;
	var password = document.getElementById("password").value;
	xmlHttp.send('name=' + name + '&password=' + password);
	xmlHttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var token=xmlHttp.getResponseHeader('Authorization');
			xmlHttp = new XMLHttpRequest();
			xmlHttp.open("GET",getUrl,true);
			xmlHttp.setRequestHeader("token", token);
			xmlHttp.send(null);
			xmlHttp.onreadystatechange = function() {
				if (this.readyState == 4 && this.status == 200) {
					document.write("Hello " + this.responseText
							+ " Welcome To GradleWebApp!");
				}
			};
		}
		if (this.status == 401) {
			document.getElementById("message").innerHTML = errorMessage;
			setTimeout(function() {
				document.getElementById("message").innerHTML = '';
				document.getElementById("name").value = '';
				document.getElementById("password").value = '';
			}, 2000);
		}
	};

}