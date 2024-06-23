const optionsBtn = document.getElementById('options');
const searchBtn = document.getElementById('chercher');
let lastClickedValue = 'origine'; // stocke la dernière valeur cliquée ("origine" ou "destination")
const applyBtn = document.getElementById('apply-btn');

applyBtn.addEventListener('click', () => {
    // Appelle l'évènement de click sur optionsBtn
    optionsBtn.click();
    searchBtn.click();
});

optionsBtn.addEventListener('click', (e) => {
    let parent = e.target.parentNode.parentNode;
    Array.from(e.target.parentNode.parentNode.classList).find((element) => {
        if (element !== "slide-up") parent.classList.add('slide-up')
        else {
            searchBtn.parentNode.classList.add('slide-up')
            parent.classList.remove('slide-up')
        }
    });
});

searchBtn.addEventListener('click', (e) => {
    let parent = e.target.parentNode;
    Array.from(e.target.parentNode.classList).find((element) => {
        if (element !== "slide-up") parent.classList.add('slide-up')
        else {
            optionsBtn.parentNode.parentNode.classList.add('slide-up')
            parent.classList.remove('slide-up')
        }
    });
});


let originMarker = null;
let destinationMarker = null;

$(function () {
    $("#origine, #destination, #rechercher-input-horaires").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "/autocomplete",
                dataType: "json",
                data: {
                    term: request.term.toLowerCase()
                },
                success: function (data) {
                    var res = data.map(function (item) {
                        var parts = item.split(';');
                        return {
                            label: parts[0],
                            value: item
                        };
                    });
                    response(res);
                }
            });
        },
        minLength: 1,
        select: function (event, ui) {
            // Complète la saisie en appuyant sur une suggestion
            this.value = ui.item.value.toString().split(";")[0];

            // Récupère les coordonnées de la station sélectionnée
            let stationInfo = ui.item.value.split(";");
            let stationName = stationInfo[0];
            let stationLatitude = parseFloat(stationInfo[2]);
            let stationLongitude = parseFloat(stationInfo[1]);

            // Vérifie si l'élément sélectionné est "origine" ou "destination"
            if (this.id === "origine") {
                // Supprime le marqueur précédent s'il existe
                if (originMarker) originMarker.remove();

                // Crée un nouveau marqueur d'origine aux coordonnées spécifiées
                originMarker = L.marker([stationLatitude, stationLongitude]).addTo(map);
                originMarker.bindPopup(`<b>Origine: ${stationName}</b>`).openPopup();
            }
            else if (this.id === "destination") {
                // Supprime le marqueur précédent s'il existe
                if (destinationMarker) destinationMarker.remove();

                // Crée un nouveau marqueur de destination aux coordonnées spécifiées
                destinationMarker = L.marker([stationLatitude, stationLongitude]).addTo(map);
                destinationMarker.bindPopup(`<b>Destination: ${stationName}</b>`).openPopup();
            }

            // Empêche la valeur sélectionnée de s'afficher dans le champ de saisie
            event.preventDefault();

            if (originMarker && destinationMarker) {
                // Obtient les coordonnées des deux marqueurs
                let originLatLng = originMarker.getLatLng();
                let destinationLatLng = destinationMarker.getLatLng();

                // Crée un groupe de points pour les deux marqueurs
                let markersGroup = new L.LatLngBounds([originLatLng, destinationLatLng]);

                // Adapte le zoom et le centrage de la carte pour afficher les deux marqueurs
                map.fitBounds(markersGroup);
            }
        }
    });
});


var map = L.map('map').setView([48.858093, 2.294694], 15);
// Définit les limites de la carte
var bounds = L.latLngBounds(
    L.latLng(48.714072, 2.077789), // Coin inférieur gauche
    L.latLng(49.004997, 2.628479)  // Coin supérieur droit
);
map.setMaxBounds(bounds);

// Fonction pour mettre à jour la variable "lastClickedValue" en fonction de l'ID du champ de saisie cliqué
function updateLastClickedValue(inputId) {
    lastClickedValue = (inputId === 'origine') ? 'origine' : 'destination';
}

// Ajoute un listener au champ de saisie "origine" pour mettre à jour "lastClickedValue"
document.getElementById('origine').addEventListener('click', () => {
    updateLastClickedValue('origine');
});

// Ajoute un listener au champ de saisie "destination" pour mettre à jour "lastClickedValue"
document.getElementById('destination').addEventListener('click', () => {
    updateLastClickedValue('destination');
});

// Fonction pour ajouter un marqueur sur la carte Leaflet lorsque l'utilisateur clique
function onMapClick(e) {
    // Ajoute un marqueur à l'emplacement cliqué en fonction de la dernière valeur cliquée ("origine" ou "destination")
    if (lastClickedValue === 'origine') {
        if (originMarker) originMarker.remove();
        originMarker = L.marker(e.latlng).addTo(map);
        originMarker.bindPopup(`<b>Origine</b>`).openPopup();
        document.getElementById('origine').value = `${e.latlng.lng}, ${e.latlng.lat}`;
    }
    else if (lastClickedValue === 'destination') {
        if (destinationMarker) destinationMarker.remove();
        destinationMarker = L.marker(e.latlng).addTo(map);
        destinationMarker.bindPopup(`<b>Destination</b>`).openPopup();
        document.getElementById('destination').value = `${e.latlng.lng}, ${e.latlng.lat}`;
    }
}

map.on('click', onMapClick);
L.tileLayer('tiles/{z}/{x}/{y}.png', {
    minZoom: 12,
    maxZoom: 16,
    attribution: 'Map data © <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
}).addTo(map);

// Code JavaScript pour gérer l'ouverture et la fermeture de la fenêtre modale
const modal = document.getElementById('modal');
const btn = document.getElementById('rechercher-btn');
const span = document.getElementById('rechercher-annuler');
const submit = document.getElementById('rechercher-submit');

submit.onclick = function () {
    $.ajax({
        url: "/schedules",
        dataType: "json",
        data: {
            station: document.getElementById('rechercher-input-horaires').value,
        },
    }).done(function (data) {
        let keys = Object.keys(data);
        // Enlève les radio boutons et items de liste existant
        $("#modal-content").html('');

        if (keys.length === 0) {
            // Pas d'horaires trouvés
            const errorDiv = document.getElementById('errorRechercher');
            errorDiv.style.display = 'block';
            setTimeout(function() {
                errorDiv.style.display = 'none';
            }, 5000); // masque l'élément après 5 secondes
            return;
        }

        $('#vers').html("<div class='vers'>Vers : </div>")

        for (let i = 0; i < keys.length; i++) {
            let station = keys[i];
            let schedules = data[station];

            // Crée un radio bouton pour chaque station
            let radioButton = $("<input>").attr({
                type: "radio",
                name: "station",
                id: "station-" + i,
                value: station
            });

            // Ajoute une image et un label pour le radio bouton
            let label = $("<label class='label_station'>").attr("for", "station-" + i).text(station.split(";")[1]);
            let imageUrl = "../css/image/M" + station.split(";")[0] + ".png";
            label.css({
                "background-image": "url(" + imageUrl + ")",
                "background-position-y": "bottom",
                "background-size": "16px 16px", // Modifiez ces valeurs pour ajuster la taille de l'image
                "padding-left": "25px", // Ajustez la valeur pour positionner correctement le texte
                "background-repeat": "no-repeat",
                "min-width": "max-content",
                "color": "rgba(255, 255, 255, 0.7)"
            });
            // Ajout du radio bouton et label dans le  modal content
            $("#modal-content").css({
                "border": "1px solid rgb(145 134 134 / 70%)",
                "background-color": " rgba(0, 0, 0, 0.4)"
            })

            $("#modal-content").append(radioButton, label);

            // Event listener pour le click sur le radio button
            radioButton.on("click", function () {
                // Enlève les items de liste existant
                $("#modal ul").html('');

                // Crée une liste non-ordonnée pour les horaires
                $('#ul_lignes').html('')
                $('#ul_lignes').css({"border": "1px solid rgb(145 134 134 / 70%) "})
                let imageUrl = "../css/image/M" + station.split(";")[0] + ".png";
                for (let j = 0; j < schedules.length; j++) {
                    let schedule = schedules[j];
                    let li = $("<li>").html(schedule).css({
                        "list-style-type": "none",
                        "background-image": "url(" + imageUrl + ")",
                        "background-size": "16px 16px", // Modifiez ces valeurs pour ajuster la taille de l'image
                        "padding-left": "25px", // Ajustez la valeur pour positionner correctement le texte
                        "background-repeat": "no-repeat",
                        "background-position": "left center"
                    });
                    $('#ul_lignes').append(li);
                }
            });
        }
    });
}

btn.onclick = function () {
    modal.style.display = "block";
}

span.onclick = function () {
    modal.style.display = "none";
}

window.onclick = function (event) {
    if (event.target == modal) {
        //modal.style.display = "none";
    }
}

// Pour déplacer l'affichage d'horaires
$(document).ready(function () {
    var isDragging = false;
    var modal = $('#modal');
    var offset = {x: 0, y: 0};

    modal.mousedown(function (e) {
        isDragging = true;
        offset.x = e.pageX - modal.offset().left;
        offset.y = e.pageY - modal.offset().top;
        modal.css('cursor', 'move');
    });

    $(document).mouseup(function () {
        isDragging = false;
        modal.css('cursor', 'default');
    });

    $(document).mousemove(function (e) {
        if (isDragging) {
            modal.offset({
                top: e.pageY - offset.y,
                left: e.pageX - offset.x
            });
        }
    });
});

const origine = document.getElementById('origine');
const destination = document.getElementById('destination');
const switchBtn = document.getElementById('switch_btn');

switchBtn.addEventListener('click', function () {
    if (origine.value != "" && destination.value != "") {
        const temp = origine.value;
        origine.value = destination.value;
        destination.value = temp;
    }
    else {
        var afficher_message = document.getElementById('chercher')
        var messageDiv = document.createElement("div");
        messageDiv.setAttribute("id", "errorSig");
        messageDiv.innerHTML = "Veuillez remplir les champs";
        afficher_message.appendChild(messageDiv);

        setTimeout(function () {
            afficher_message.removeChild(messageDiv);
        }, 3000);
    }
});