console.log("--- executing js");

function loadRestaurants() {
    let url = 'http://localhost:8080/restaurants?names=true';

    fetch(url)
        .then(data => {
            return data.json();
        })
        .then(res => {
            console.log(res);
            document.querySelector('#conteudo').innerHTML = JSON.stringify(res);
        })
        .catch(error => {
            document.querySelector('#conteudo').innerHTML = `Error fetch restaurants: {error}`;
        })
}

document.querySelector('.btn').addEventListener('click', e => {
    console.log('io');
    loadRestaurants();
});


