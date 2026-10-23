// HTTP Basic Auth kennt kein Logout: Eine Anfrage mit ungültigen Zugangsdaten
// überschreibt die im Browser gespeicherten, danach fragt der Browser beim Aufruf
// der Startseite erneut nach Benutzer und Passwort.
function logout(target) {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", target, true, "logout", "logout");
	xhr.onloadend = function () {
		window.location.href = target;
	};
	xhr.send();
	return false;
}
