
function validateForm() {
    var username = document.getElementById("nume_utiz").value;
    var password = document.getElementById("parola").value;
    var errorMsg = '';

    if (username === '') {
        errorMsg += 'Nume de utilizator este necesar.\n';
    }

    if (password === '') {
        errorMsg += 'Parola este necesară.\n';
    }

      if (errorMsg) {
        alert(errorMsg);
        return false;
    } else {
        return true;
    }
	
}
