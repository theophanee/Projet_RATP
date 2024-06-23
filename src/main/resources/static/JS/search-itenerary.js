var itineraries;
const DEPART = "Départ";
const FIN = "Arrivée";
const LIGNE = "ligne";
const WALK_CLASS_HTML = '<img class="marcher" src="../css/image/marche.png" alt="">'

var stationsByLocalisation = new Map();
var itineraryLayer = L.layerGroup();
var time

window.addEventListener('load', function () {
    fillCurrentHour();
});

function fillCurrentHour() {
    var date = new Date();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    if (minutes < 10) minutes = "0" + minutes;
    time = hours + ":" + minutes;
    document.getElementById('hour').value = time;
}

$(document).ready(function () {
    $('#search-btn').click(function () {
        document.getElementById('liste').style.display = "block"

        let dataToSend = getFormData();
        if (dataToSend === null) return;
        let urlQuery = dataToSend[0];
        let data = dataToSend[1];

        // Envoie les données au backend via une requête AJAX
        $.ajax({
            type: 'GET',
            url: urlQuery,
            data: data,
            // Traitement de la réponse du backend en cas de succès
            success: function (response) {
                $("#liste").css({
                        "border": "2px solid rgb(145 134 134 / 70%)"
                    })

                // Cache les résultats d'une recherche précédente
                for (var i = 1; i <= 5; i++) document.getElementById("det" + i).style.display = "none";

                itineraries = response
                if (itineraries.length === 0) alert("Station inexistantes dans le réseau de transport")
                for (let index = 0; index < itineraries.length; index++) {
                    buildPictogrammeTraject(index + 1);
                    addTrajectDetails(index + 1);
                    displayTraject(index + 1);
                }
                pingLocalizations(1);
            },
            // Traitement de la réponse du backend en cas d'erreur
            //TODO?
            error: function (xhr, status, error) {
                //console.log("failed")
                //console.log(xhr.responseText);
                // Faire quelque chose en cas d'erreur
            }
        });
    });

    // Construit le trajet pictogramme
    function buildPictogrammeTraject(index) {
        // Récupère le indexième résultat
        var trajectResult = document.querySelectorAll('.resultat')[index - 1];
        var itinerary = itineraries[index - 1];

        //Construction du trajet de pictogrammes
        var htmlContent = '<div class="testAffichage"><div class="ligne-container">';
        var linesNames = [];
        itinerary.forEach(section => {
            let valToPush ="";
            if (section.ligne != null) valToPush = section.ligne.nomLigne.split(' ')[0];
            else if(section.distance > 0) valToPush = 'sectionMarche';

            if(valToPush !== "" && linesNames[linesNames.length - 1] !== valToPush) linesNames.push(valToPush);
        });
        linesNames.forEach(line => {
            if (line === 'sectionMarche') htmlContent += WALK_CLASS_HTML;
            else htmlContent += '<img src="../css/image/M' + line + '.png" alt="ligne ' + line + '" class="ligne" style="height: 30px;">';
        })

        htmlContent += '</div><div class="dureeTrajet">' + buildDuration(index) + '</div></div>';
        htmlContent += '<div class="details" id="details' + index + '"style="display: none"></div>';
        trajectResult.innerHTML = (htmlContent)
    }

    function buildDuration(index) {
        var itinerary = itineraries[index - 1];
        var horaire_depart = parseISO8601Time(itinerary[0].depart.horaireDePassage);
        var horaire_arrivee = parseISO8601Time(itinerary[itinerary.length - 1].arrivee.horaireDePassage);

        // Si l'horaire d'arrivée est inférieur à l'horaire de départ, on ajoute 1 jour à l'horaire d'arrivée (on arrive le lendemain)
        var traject_duration = horaire_arrivee > horaire_depart ? new Date(Math.abs(horaire_arrivee - horaire_depart)) : new Date(Math.abs(horaire_arrivee.setDate(horaire_arrivee.getDate() + 1) - horaire_depart));
        return traject_duration.getUTCHours() !== 0 ? traject_duration.getUTCHours() + " h " + traject_duration.getUTCMinutes() + ' min' : traject_duration.getUTCMinutes() + ' min';
    }

    function parseISO8601Time(timeString) {
        const [hours, minutes, seconds] = timeString.split(':').map(Number);
        const date = new Date();
        date.setHours(hours);
        date.setMinutes(minutes);
        date.setSeconds(seconds);
        return date;
    }

    function addTrajectDetails(index) {
        var trajectDetails = document.getElementById("details" + index);
        var itinerary = itineraries[index - 1];

        var detailsHtml = '<div class="detailsTrajet">'
        var stationsNames = [];
        itinerary.forEach(section => {
            // Ajoute les stations du trajets
            if (section.ligne != null) {
                // Vérifie si on change de ligne pour ajouter la station finale sur la ligne courante
                if (stationsNames.length > 0) {
                    var lastElement = stationsNames[stationsNames.length - 1];
                    if (lastElement.split(';')[1] !== section.ligne.nomLigne.split(' ')[0])
                        stationsNames.push(section.depart.nomLieu + ";" + section.ligne.nomLigne.split(' ')[0] + ";" + section.depart.horaireDePassage);
                }
                stationsNames.push(section.arrivee.nomLieu + ";" + section.ligne.nomLigne.split(' ')[0] + ";" + section.arrivee.horaireDePassage);
            }
            // Ajoute les distance de marche
            else {
                if (section.depart.nomLieu === DEPART && section.arrivee.nomLieu !== FIN) stationsNames.push("Départ");
                if (section.distance > 0) stationsNames.push("Marcher pendant " + section.distance*1000 + " mètres");
                if (section.depart.nomLieu !== DEPART && section.arrivee.nomLieu === FIN) stationsNames.push("Arrivée");
            }
        })

        var oldNumLigne = stationsNames[0].split(';')[1];
        var currentDiv = '<div class="groupe groupe' + oldNumLigne + '">';
        stationsNames.forEach(station => {
            var numLigne = station.split(';')[1];
            var stationName = station.split(';')[0];

            if (numLigne !== oldNumLigne) {
                // Ferme le div précédent s'il existe
                if (currentDiv !== '') currentDiv += '</div>';

                // Crée un nouveau div pour le nouvel numLigne
                currentDiv += '<div class="groupe groupe' + numLigne + '">';
                oldNumLigne = numLigne;
            }

            var horaireDePassage = station.split(';')[2]
            if (horaireDePassage != null) {
                var heure = horaireDePassage.split(":")[0]
                var min = horaireDePassage.split(":")[1]
                currentDiv += '<div class="station nomStation' + numLigne + '">' + heure + "h" + min + " : " + stationName + '</div>';
            }
            else currentDiv += '<div class="station nomStation' + numLigne + '">' + stationName + '</div>';
        });

        if (currentDiv !== '') currentDiv += '</div>';
        detailsHtml += currentDiv;
        trajectDetails.innerHTML = (detailsHtml)
    }

    function parseISO8601Duration(durationString) {
        const durationRegex = /P((\d+)Y)?((\d+)M)?((\d+)D)?T?((\d+)H)?((\d+)M)?((\d+)S)?/;
        const matches = durationString.match(durationRegex);
        const years = matches[2] ? parseInt(matches[2]) : 0;
        const months = matches[4] ? parseInt(matches[4]) : 0;
        const days = matches[6] ? parseInt(matches[6]) : 0;
        const hours = matches[8] ? parseInt(matches[8]) : 0;
        const minutes = matches[10] ? parseInt(matches[10]) : 0;
        const seconds = matches[12] ? parseInt(matches[12]) : 0;
        const totalSeconds =
            years * 31536000 +
            months * 2592000 +
            days * 86400 +
            hours * 3600 +
            minutes * 60 +
            seconds;
        return totalSeconds;
    }

    function durationMinutes(durationString) {
        return Math.ceil(parseISO8601Duration(durationString) / 60);
    }
})


// Déclare des tableaux globaux pour les markers et polylines
var itineraryMarkers = [];
var itineraryPolylines = [];

function pingLocalizations(index) {
    // Enlève les précédents markers et polylines de la carte
    itineraryMarkers.forEach(marker => marker.remove());
    itineraryPolylines.forEach(polyline => polyline.remove());

    // Efface le contenu des tableaux pour le nouvel itinéraire
    itineraryMarkers = [];
    itineraryPolylines = [];

    var itinerary = itineraries[index - 1];
    if (itinerary === undefined) return;
    var prevValues = null;

    // Trajet à pied
    if(itinerary.length == 1) {
        let polyline = L.polyline([
            [itinerary[0].depart.localisation.latitude, itinerary[0].depart.localisation.longitude],
            [itinerary[0].arrivee.localisation.latitude, itinerary[0].arrivee.localisation.longitude]
        ],{color: 'black',
            weight: 8,
            dashArray: [10, 20]
        }).addTo(map);
        itineraryPolylines.push(polyline);
        return;
    }
    itinerary.forEach(section => {
        let lineColor;
        let lignetmp;
        // Section de marche au milieu d'un trajet
        if (section.depart.nomLieu === DEPART && section.arrivee.nomLieu === FIN) {
            if (prevValues) {
                let latitude = section.depart.localisation.latitude;
                let longitude = section.depart.localisation.longitude;
                let nom_station = section.depart.nomLieu;
                const marker = L.marker([latitude, longitude]).addTo(map)
                    .bindPopup(`<b>${nom_station}</b>`).openPopup();
                itineraryMarkers.push(marker);
                const style = window.getComputedStyle(document.documentElement);
                lineColor = style.getPropertyValue('--ligne' + prevValues.ligne);
                polyline = L.polyline([prevValues.marker.getLatLng(), marker.getLatLng()], {
                    color: lineColor,
                    weight: 8
                }).addTo(map);
                itineraryPolylines.push(polyline);
            }

            // Relie le départ et l'arrivée avec des pointillés
            polyline = L.polyline([
                [section.depart.localisation.latitude, section.depart.localisation.longitude],
                [section.arrivee.localisation.latitude, section.arrivee.localisation.longitude]
            ],{color: 'black',
                weight: 8,
                dashArray: [10, 20]
            }).addTo(map);
            itineraryPolylines.push(polyline);
            prevValues = null;
        }
        // Section de transport, ou marche vers transport/lieu
        else {
            // Vérifie si la ligne existe, sinon c'est un trajet à pied
            if (section.ligne == null) lignetmp = {nomLigne: "sectionMarche"};
            else lignetmp = section.ligne.nomLigne.split(' ')[0];

            let latitude = section.depart.localisation.latitude;
            let longitude = section.depart.localisation.longitude;
            let nom_station = section.depart.nomLieu;
            const marker = L.marker([latitude, longitude]).addTo(map)
                .bindPopup(`<b>${nom_station}</b>`).openPopup();
            itineraryMarkers.push(marker);

            if (prevValues) {
                let polyline;
                if (prevValues.ligne.nomLigne === 'sectionMarche') {
                    polyline = L.polyline([prevValues.marker.getLatLng(), marker.getLatLng()], {
                        color: 'black',
                        weight: 8,
                        dashArray: [10, 20]
                    }).addTo(map);
                }
                else {
                    const style = window.getComputedStyle(document.documentElement);
                    lineColor = style.getPropertyValue('--ligne' + prevValues.ligne);
                    polyline = L.polyline([prevValues.marker.getLatLng(), marker.getLatLng()], {
                        color: lineColor,
                        weight: 8
                    }).addTo(map);
                }
                itineraryPolylines.push(polyline);
            }

            let polyline2;
            if (section.arrivee.nomLieu !== FIN) prevValues = {marker: marker, ligne: lignetmp};
            // S'il faut marcher à la fin
            else if (section.ligne == null) {
                let latitude = section.arrivee.localisation.latitude;
                let longitude = section.arrivee.localisation.longitude;
                let nom_station = FIN;
                const markerARR = L.marker([latitude, longitude]).addTo(map)
                    .bindPopup(`<b>${nom_station}</b>`).openPopup();
                itineraryMarkers.push(markerARR);
                polyline2 = L.polyline([marker.getLatLng(), markerARR.getLatLng()], {
                    color: 'black',
                    weight: 8,
                    dashArray: [10, 20]
                }).addTo(map);
                itineraryPolylines.push(polyline2);
            }
        }
    });
}

function AfficheDetails(num) {
    var message = document.getElementById("details" + num);

    var allDetailsDivs = document.querySelectorAll(".details");
    for (var i = 0; i < allDetailsDivs.length; i++)
        if (allDetailsDivs[i].style.display === "block" && allDetailsDivs[i] !== message)
            allDetailsDivs[i].style.display = "none";

    if (message.style.display === "none") {
        message.style.display = "block"; // Affiche le message
        pingLocalizations(num);
    }
    else message.style.display = "none"; // Cache le message
}

function displayTraject(idTraject) {
    let name = "det" + idTraject.toString();
    document.getElementById(name).style.display = "block";
}

function isEmpty(field) {
    if (field === '' || field === null || field === undefined) {
        var afficher_message = document.getElementById('chercher')
        var messageDiv = document.createElement("div");
        messageDiv.setAttribute("id", "errorSig");
        messageDiv.innerHTML = "Veuillez remplir les champs";
        afficher_message.appendChild(messageDiv);

        setTimeout(function () {
            afficher_message.removeChild(messageDiv);
        }, 5000);

        return true;
    }
    return false;
}

function checkEmptyAndAlert(field, message) {
    if (field === '' || field === null || field === undefined) {
        var afficher_message = document.getElementById('chercher');
        var messageDiv = document.createElement("div");
        messageDiv.setAttribute("id", "errorSig");
        messageDiv.innerHTML = "Veuillez remplir les champs " + message;
        afficher_message.appendChild(messageDiv);

        setTimeout(function () {
            afficher_message.removeChild(messageDiv);
        }, 5000);

        return true;
    }
    return false;
}

// Fonction qui permet de récupérer les données à envoyer à l'API selon l'option de trajet sélectionnée
function getFormData() {
    var origine = $('#origine').val();
    var destination = $('#destination').val();
    var timeValue = $('#hour').val();

    if (isEmpty(origine) || isEmpty(destination) || isEmpty(timeValue)) return;

    var selectedOption = $('input[name="typetrajet"]:checked').val();
    var data = {}
    var url = ''
    if (selectedOption === 'lazy0') {
        url = 'itinerary/optimal'
        data = {
            "origin": origine,
            "destination": destination,
            "time": timeValue
        }
    }
    else if (selectedOption === 'lazy1') {
        var distanceMax = $('#lazy_distance').val();
        if (checkEmptyAndAlert(distanceMax, 'distance maximale')) return null;
        url = 'itinerary/lazy'
        data = {
            "origin": origine,
            "destination": destination,
            "time": timeValue,
            "distanceMax": distanceMax
        }
    }
    else if (selectedOption === 'sport0') {
        url = 'itinerary/fullSport'
        data = {
            "origin": origine,
            "destination": destination,
            "time": timeValue
        }
    }
    else if (selectedOption === 'sport1') {
        var distanceMin = $('#sport_distance').val();
        if (checkEmptyAndAlert(distanceMin, 'distance minimale')) return null;
        url = 'itinerary/sport/distance'
        data = {
            "origin": origine,
            "destination": destination,
            "time": timeValue,
            "distanceMin": distanceMin
        }
    }
    else if (selectedOption === 'sport2') {
        var walkingTimeMin = $('#sport_minutes').val();
        if (checkEmptyAndAlert(walkingTimeMin, 'temps de marche')) return null;
        url = 'itinerary/sport/time'
        data = {
            "origin": origine,
            "destination": destination,
            "time": timeValue,
            "walkingTimeMin": walkingTimeMin
        }
    }
    return [url, data]
}