//save and load as file
/*
 Adapted from http://www.html5rocks.com/en/tutorials/file/dndfiles/#toc-reading-files
*/
//test: filesupport availiable

//reader


window.onload = function () {

    $("#graph_imp").click(function () {
        $('#file').click();
    });

    var input = document.getElementById("file");
    input.addEventListener("change", function (e) {
        var file = e.target.files[0];

        // Only render plain text files
        if (!file.type === "text/plain")
            return;

        var reader = new FileReader();

        reader.onload = function (event) {
            var log = [];
            var data = event.target.result;
            //Test if Json
            if (!isJson(event.target.result)) {
                document.getElementById("code").innerHTML = "";

                log.push("Die geladene Datei ist nicht im JSON-Format");
                document.getElementById("code").appendChild(preparelog(log));
            } else {
                try {
                    document.getElementById("code").removeChild(document.getElementById("list_error"));
                } catch (e) {
                }
                try {
                    graph.fromJSON(JSON.parse(data));
                    refreshDia();
                } catch (e) {
                    log.push("Die JSON-Datei konnte nicht geladen werden!");
                    document.getElementById("code").appendChild(preparelog(log));
                }
            }
        };
        reader.readAsText(file);
    });


    function enterFullscreen(element) {
        if (element.requestFullscreen) {
            element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
            element.msRequestFullscreen();
        } else if (element.webkitRequestFullscreen) {
            element.webkitRequestFullscreen();
        }
    }

    function exitFullscreen() {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        }
    }

    console.log("outside");
    $("#fullscreenStart").click(function () {
        console.log("fullstart");
        enterFullscreen(document.getElementById("tool")); // ein bestimmtes Element
    });

    $("#fullscreenEnd").click(function () {
        exitFullscreen();
    });

};

function writeActivityDiagram() {
    var sendForm = $('#sendForm');
    sendForm.attr('action', sendForm.attr('action') + "?lang=python");
    // FIXME: language!
    $('#learnerSolution').val($('#preCode').html());
}

//Test: Is Json Objrect
function isJson(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

//writer via FileSaver.js --->  https://github.com/eligrey/FileSaver.js
function saveGraphAsTxt() {
    var input = JSON.stringify(graph.toJSON());
    var blob = new Blob([input], {type: "text/plain;charset=utf-8"});
    saveAs(blob, "graph.txt");
}
