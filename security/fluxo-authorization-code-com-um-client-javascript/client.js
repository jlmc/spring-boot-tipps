const config = {
    clientId: "food4uAnalytics",
    clientSecret: "food4uAnalytics",
    authorizeUrl: "http://authorization.server.local:8081/oauth/authorize",
    tokenUrl: "http://authorization.server.local:8081/oauth/token",
    callbackUrl: "http://food4u.local:8000",
    booksUrl: "http://food4u.local:8080/books"

};

let accessToken = "";

// Support for PKCE code flow
// --------
function generateCodeVerifier() {
    let codeVerifier = generateRandomString(128);
    localStorage.setItem("codeVerifier", codeVerifier);

    return codeVerifier;
}

function generateRandomString(length) {
    let text = "";
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (let i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
}

function generateCodeChallenge(codeVerifier) {
    return base64URL(CryptoJS.SHA256(codeVerifier));
}

function getCodeVerifier() {
    return localStorage.getItem("codeVerifier");
}

function base64URL(string) {
    return string.toString(CryptoJS.enc.Base64).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}

// --------

function cunsultResource() {
    alert("Consult Resource com access token " + accessToken);

    $.ajax({
        url: config.booksUrl,
        type: "get",

        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + accessToken);
        },

        success: function (response) {
            var json = JSON.stringify(response);
            $("#resultado").text(json);
        },

        error: function (error) {
            alert("Erro ao cunsultResource recurso");
        }
    });
}

function gerarAccessToken(code) {
    alert("Gerar access token com code " + code);

    let codeVerifier = getCodeVerifier();

    let clientAuth = btoa(config.clientId + ":" + config.clientSecret);

    let params = new URLSearchParams();
    params.append("grant_type", "authorization_code");
    params.append("code", code);
    params.append("redirect_uri", config.callbackUrl);
    params.append("code_verifier", codeVerifier);

    $.ajax({
        url: config.tokenUrl,
        type: "post",
        data: params.toString(),
        contentType: "application/x-www-form-urlencoded",

        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Basic " + clientAuth);
        },

        success: function (response) {
            accessToken = response.access_token;

            alert("Access token gerado: " + accessToken);
        },

        error: function (error) {
            console.log("---" + JSON.stringify(error));
            alert("Erro ao gerar access key: " + error);
        }
    });
}

function login() {

    let codeVerifier = generateCodeVerifier();
    let codeChallenge = generateCodeChallenge(codeVerifier);

    // https://auth0.com/docs/protocols/oauth2/oauth-state
    let state = btoa(Math.random());
    localStorage.setItem("clientState", state);

    //window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}&code_challenge_method=s256&code_challenge=${codeChallenge}`;

    window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}&code_challenge_method=s256&code_challenge=${codeChallenge}`;

}

$(document).ready(function () {
    let params = new URLSearchParams(window.location.search);

    let code = params.get("code");
    let state = params.get("state");
    let currentState = localStorage.getItem("clientState");

    if (code) {
        // window.history.replaceState(null, null, "/");

        if (currentState == state) {
            gerarAccessToken(code);
        } else {
            alert("State inválido");
        }
    }
});

$("#btn-consultar").click(cunsultResource);
$("#btn-login").click(login);