$(document).ready(function () {
    $('#testBtn').click(testSol);
});

/**
 * @param {object} textContent
 * @param {boolean} textContent.success
 * @param {string} textContent.awaited
 * @param {string | null} textContent.found
 * @return {string}
 */
function renderTextResult(textContent) {
    if (textContent.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> Das Element hat den richtigen Textinhalt.</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> Das Element hat nicht den richtigen Textinhalt.
    <ul>
        <li>Gesucht war:    &quot;${textContent.awaited}&quot;</li>
        <li>Gefunden wurde ${textContent.found != null ? (': &quot;' + textContent.found + '&quot;') : '<b>Kein Textinhalt!</b>'}&quot;</li>
    </ul>
</p>`.trim();
    }
}

/**
 * @param {object} attrResult
 * @param {boolean} attrResult.success
 * @param {string} attrResult.attrName
 * @param {string} attrResult.awaited
 * @param {string | null} attrResult.found
 * @return {string}
 */
function renderAttrResult(attrResult) {
    if (attrResult.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> Das Attribut &quot;${attrResult.attrName}&quot; hat den richtigen Wert.</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> Das Attribut &quot;${attrResult.attrName}&quot; hat nicht den richtigen Wert.
    <ul>
        <li>Gesucht war:    &quot;${attrResult.awaited}&quot;</li>
        <li>Gefunden wurde ${attrResult.found != null ? (': &quot;' + attrResult.found + '&quot;') : ' <b>kein Attribut!</b>'}</li>
    </ul>
</p>`.trim();
    }
}

/**
 * @param {object} result
 * @param {int} result.id
 * @param {int} result.points
 * @param {int} result.maxPoints
 * @param {success} result.success
 * @param {boolean} result.elementFound
 * @param {object | null} result.textContent
 * @param {object[]} result.attributeResults
 * @return {string}
 */
function renderHtmlResult(result) {
    let subHtml = `<div class="alert alert-${result.success ? 'success' : 'danger'}">`;

    subHtml += `<b>Teilaufgabe ${result.id}</b>: ${$('#text_' + result.id).text()} (${ result.points} / ${result.maxPoints} Punkte)<hr>`;

    if (result.elementFound) {
        subHtml += `<p><span class="glyphicon glyphicon-ok"></span> Das Element konnte gefunden werden.</p>`;
    } else {
        subHtml += `<p><span class="glyphicon glyphicon-remove"></span> Das Element konnte nicht gefunden werden!</p>`;
    }

    if (result.textContent !== null) {
        subHtml += renderTextResult(result.textContent);
    }

    for (let attrResult of result.attributeResults) {
        subHtml += renderAttrResult(attrResult);
    }

    subHtml += `</div>`;
    return subHtml;
}

/**
 * @param {object} condRes
 * @param {int} condRes.points
 * @param {int} condRes.maxPoints
 * @param {boolean} condRes.success
 * @param {string} condRes.description
 * @param {string} condRes.awaited
 * @param {string} condRes.gotten
 *
 * @return {string}
 */
function renderConditionResult(condRes) {
    if (condRes.success) {
        return `<p><span class="glyphicon glyphicon-ok"></span> ${condRes.description}&quot;</p>`
    } else {
        return `
<p><span class="glyphicon glyphicon-remove"></span> ${condRes.description}&quot;
    <ul>
        <li>Gesucht war:    &quot;${condRes.awaited}&quot;</li>
        <li>Gefunden wurde aber: &quot;${condRes.gotten}&quot;</li>
    </ul>
</p>`.trim();
    }
}

/**
 * @param {object} result
 * @param {int} result.id
 * @param {int} result.points
 * @param {int} result.maxPoints
 * @param {boolean} result.success
 * @param {object[]} result.preResults
 * @param {string} result.actionDescription
 * @param {boolean} result.actionPerformed
 * @param {object[]} result.postResults
 *
 * @return {string}
 */
function renderJsResult(result) {
    let subHtml = `<div class="alert alert-${result.success ? 'success' : 'danger'}">`;

    subHtml += `<b>Test ${result.id}:</b> (${ result.points} / ${result.maxPoints} Punkte)<hr>`;

    for (let preResult of result.preResults) {
        subHtml += renderConditionResult(preResult);
    }

    if (result.actionDescription) {
        subHtml += `<p><span class="glyphicon glyphicon-ok"></span> ${result.actionDescription}&quot;</p>`;
    } else {
        subHtml += `<p><span class="glyphicon glyphicon-remove"></span> ${result.actionDescription}&quot;</p>`;
    }

    for (let postResult of result.postResults) {
        subHtml += renderConditionResult(postResult);
    }

    subHtml += ` </div>`;
    return subHtml;
}

/**
 * @param {object[]} results
 * @param {function} renderFunc
 * @return {string}
 */
function renderResults(results, renderFunc) {
    let html = '';

    for (let i = 0; i < results.length; i = i + 3) {
        let secondToRender = results[i + 1] || null;
        let thirdToRender = results[i + 2] || null;
        html += `
<div class="row">
    <div class="col-md-4">${renderFunc(results[i])}</div>
    <div class="col-md-4">${secondToRender === null ? '' : renderFunc(secondToRender)}</div>
    <div class="col-md-4">${thirdToRender === null ? '' : renderFunc(thirdToRender)}</div>
</div>`.trim();
    }

    return html;
}

/**
 *
 * @param {object} corr
 * @param {boolean} corr.solutionSaved
 * @param {string} corr.part
 * @param {success} corr.success
 * @param {int} corr.points
 * @param {int} corr.maxPoints
 * @param {object[]} corr.htmlResults
 * @param {object[]} corr.jsResults
 */
function onWebCorrectionSuccess(corr) {
    let html = '';

    if (corr.solutionSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (corr.success) {
        html += `<div class="alert alert-success">Ihre Lösung war komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
    } else {
        if (corr.points < .75 * corr.maxPoints) {
            html += `<div class="alert alert-danger">Ihre Lösung war nicht komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
        } else {

            html += `<div class="alert alert-warning">Ihre Lösung war nicht komplett richtig. Sie haben ${corr.points} von ${corr.maxPoints} erreicht.</div>`;
        }

        if (corr.part === 'html') {
            html += renderResults(corr.htmlResults, renderHtmlResult);
        } else {
            html += renderResults(corr.jsResults, renderJsResult);
        }

    }

    $('#correction').html(html);
    $('#correctionTabBtn').click();
    $('#testBtn').prop('disabled', false);
}

/**
 * @param jqXHR {{responseText: string, responseJSON: string}}
 */
function onWebCorrectionError(jqXHR) {
    $('#testBtn').prop('disabled', false);

    $('#correction').html(`
<div class="alert alert-danger">Es gab einen Fehler bei der Korrekur:
    <hr>
    <pre>${jqXHR.responseJSON.msg}</pre>
</div>`.trim())
}

function testSol() {
    let exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive("web", exerciseId, exercisePart).url;

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify({solution: editor.getValue()}),
        async: true,
        success: onWebCorrectionSuccess,
        error: onWebCorrectionError
    });
}

/**
 * @param {string} escapedHTML
 */
function unescapeHTML(escapedHTML) {
    return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

function updatePreviewNew() {
    let exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.updateWebSolution(exerciseId, exercisePart).url;

    $.ajax({
        type: 'PUT',
        //     dataType: 'json', // return type
        contentType: 'text/plain', // type of message to server, "pure" html
        url,
        data: unescapeHTML(editor.getValue()),
        async: true,
        success: function (response) {
            $('#preview').attr('src', function (i, val) {
                // Refresh iFrame
                return val;
            });
        },
        error: function (jqXHR) {
            console.error(jqXHR);
        }
    });
}