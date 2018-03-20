//save and load as file
/*
 Adapted from http://www.html5rocks.com/en/tutorials/file/dndfiles/#toc-reading-files
*/
let loaded_graph;


/**
 *
 * @param result
 * @param {int} result.id
 * @param {boolean} result.correct
 * @param {string} result.evaluated
 * @param {string} result.awaited
 * @param {string} result.gotten
 */
function renderProgResult(result) {
    return `
<div class="panel panel-${result.correct ? 'success' : 'danger'}">
    <div class="panel-heading">${result.id}. Test von <code>${result.evaluated}</code> war ${result.correct ? '' : ' nicht'} erfolgreich.</div>
    <div class="panel-body">
        <p>Erwartet: <code>${result.awaited.length === 0 ? '""' : result.awaited}</code></p>
        <p>Bekommen: <code>${result.gotten.length === 0 ? '""' : result.gotten}</code></p>
    </div>
</div>`.trim();
}

/**
 * @param {object} response
 * @param {boolean} response.solutionSaved
 * @param {object[]} response.results
 */
function onUmlActivityCorrectionSuccess(response) {
    console.log(JSON.stringify(response, null, 2));

    let html = `<div class="alert alert-${response.solutionSaved ? 'success' : 'danger'}">Ihre Lösung wurde ${response.solutionSaved ? '' : ' nicht'} gespeichert.</div>`;

    for (let i = 0; i < response.results.length; i = i + 3) {
        let firstResult = response.results[i] || null;
        let secondResult = response.results[i + 1] || null;
        let thirdNextResult = response.results[i + 2] || null;

        html += `
<div class="row">
    <div class="col-md-4">${firstResult != null ? renderProgResult(firstResult) : ''}</div>
    <div class="col-md-4">${secondResult != null ? renderProgResult(secondResult) : '' }</div>
    <div class="col-md-4">${thirdNextResult != null ? renderProgResult(thirdNextResult) : ''}</div>
</div>`.trim();

    }
    $('#correction').html(html);

    $('#ExerciseText').collapse('hide');
    $('#Configuration').collapse('hide');
    $('#generatedCode').collapse('hide');
    $('#resultDiv').collapse('show');
    $('#testButton').prop('disabled', false);
}


function onUmlActivityCorrectionError(jqXHR) {
    console.log(jqXHR.responseText);
    $('#testButton').prop('disabled', false);
}

function testSol() {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    $('#testButton').prop('disabled', true);

    let dataToSend = {
        exercisePart,
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

$(document).ready(function () {
    $('#testButton').click(testSol);
});

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
