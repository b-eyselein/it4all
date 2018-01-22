//save and load as file
/*
 Adapted from http://www.html5rocks.com/en/tutorials/file/dndfiles/#toc-reading-files
*/
let loaded_graph;

function onUmlActivityCorrectionSuccess(response) {
    $('#result').html(response);
    $('#ExerciseText').collapse('hide');
    $('#Configuration').collapse('hide');
    $('#generatedCode').collapse('hide');
    $('#resultDiv').collapse('show');
    $('#sendToServer').prop('disabled', false);
}

function onUmlActivityCorrectionError(jqXHR) {
    console.log(jqXHR.responseText);
    $('#sendToServer').prop('disabled', false);
}

function testSol(url, part) {
    $('#sendToServer').prop('disabled', true);

    let dataToSend = {
        part,
        solution: $('#preCode').text()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onUmlActivityCorrectionSuccess,
        error: onUmlActivityCorrectionError
    });
}

window.onload = function () {

    $("#startFullscreen").click(function () {
        enterFullscreen(document.getElementById("fullscreen"));
    });
    $("#endFullscreen").click(function () {
        exitFullscreen();
    });
    $('#file').click(function () {
        const input = document.getElementById("file");
        input.addEventListener("change", function (e) {
            const file = e.target.files[0];

            // Only render plain text files
            if (!file.type === "text/plain")
                return;

            const reader = new FileReader();

            reader.onload = function (event) {
                loaded_graph = event.target.result;
            };
            reader.readAsText(file);
        });
    });

    $("#graph_import").click(function () {
        try {
            const parsed = JSON.parse(loaded_graph);

            graph.fromJSON(parsed.graph);
            parentChildNodes = parsed.parentChildNodes;

            rebuildGraph();

        } catch (e) {
            // Not json...
            $('#generationAlerts').html('<div class="alert alert-danger">Die geladene Datei ist nicht im JSON-Format!</div>');
        }
    });

};

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
    let input = {"graph": graph.toJSON(), "parentChildNodes": parentChildNodes};
    input = JSON.stringify(input);
    // var input = JSON.stringify(graph.toJSON());
    const blob = new Blob([input], {type: "text/plain;charset=utf-8"});
    saveAs(blob, "graph.json");
}

function enterFullscreen(element) {
    localStorage.setItem("parentChildNodes", JSON.stringify(parentChildNodes));
    const currentGraph = graph.toJSON();
    if (element.requestFullscreen) {
        element.requestFullscreen();
    } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
    } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    }
    graph.fromJSON(currentGraph);
    parentChildNodes = JSON.parse(localStorage.getItem("parentChildNodes"));
    reSetSelection();
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