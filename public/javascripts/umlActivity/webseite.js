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

//writer via FileSaver.js --->  https://github.com/eligrey/FileSaver.js
function saveGraphAsTxt() {
    const graphInput = {"graph": graph.toJSON(), "parentChildNodes": parentChildNodes};
    const blob = new Blob([JSON.stringify(graphInput)], {type: "text/plain;charset=utf-8"});
    saveAs(blob, "graph.json");
}
