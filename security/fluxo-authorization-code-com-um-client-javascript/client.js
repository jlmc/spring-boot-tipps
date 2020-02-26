const config = {
    clientId: "food4uAnalytics",
    clientSecret: "food4uAnalytics",
    authorizeUrl: "http://authorization.server.local:8081/oauth/authorize",
    tokenUrl: "http://authorization.server.local:8081/oauth/token",
    callbackUrl: "http://food4u.local:8000",
    booksUrl: "http://food4u.local:8080/books"

};

let accessToken = "";

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

    let clientAuth = btoa(config.clientId + ":" + config.clientSecret);

    let params = new URLSearchParams();
    params.append("grant_type", "authorization_code");
    params.append("code", code);
    params.append("redirect_uri", config.callbackUrl);

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
    // https://auth0.com/docs/protocols/oauth2/oauth-state
    let state = btoa(Math.random());
    localStorage.setItem("clientState", state);

    window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}`;
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