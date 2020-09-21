function simplePage() {

    let now = new Date();

    document.getElementById("testDiv").innerHTML = '<div style="text-align:center;">'
        + '<h1>Welcome to my API powered website!</h1>'
        + '<img src="/client/img/quartet.jpg"  alt="Logo"/>'
        + '<div style="font-style: italic;">'
        + 'Generated at ' + now.toLocaleTimeString()
        + '</div>'
        + '</div>';

}

function pageLoad() {

    //resetLoginForm();
    resetLoginForm();
}

function resetLoginForm() {
    /*if (Cookies.get("destination") === undefined) {
        window.location.href = "/client/CharacterList.html";
    }*/

    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener("submit", event => {
        event.preventDefault();

        let formData = new FormData(loginForm);
        alert(formData.toString());
        fetch('/user/login', {method: 'post', body: formData}
        ).then(response => response.json()
        ).then(data => {
                if (data.hasOwnProperty('error')) {
                    alert(data.error);
                } else {
                    Cookies.set("sessionToken", data.token);
                    window.location.href = "/client/index.html"; //Cookies.get("destination");
                }
            }
        );
    });

}

FB.login(function(response) {
    if (response.authResponse) {
        console.log('Welcome!  Fetching your information.... ');
        FB.api('/me', function(response) {
            console.log('Good to see you, ' + response.name + '.');
        });
    } else {
        console.log('User cancelled login or did not fully authorize.');
    }
});


