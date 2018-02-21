$(document).ready(function () {
    $('#testButton').click(testSol);
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
 * @param {success} result.success
 * @param {boolean} result.elementFound
 * @param {object | null} result.textContent
 * @param {object[]} result.attributeResults
 * @return {string}
 */
function renderHtmlResult(result) {
    let subHtml = `<div class="alert alert-${result.success ? 'success' : 'danger'}">`;

    subHtml += '<b>Teilaufgabe ' + result.id + '</b>: ' + $('#text_' + result.id).text() + '<hr>';

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

function renderJsResult(result) {
    // let subhtml = "";
    // TODO: implement!
    return '';
}

/**
 *
 * @param {object} corr
 * @param {boolean} corr.solutionSaved
 * @param {success} corr.success
 * @param {int} corr.points
 * @param {int} corr.maxPoints
 * @param {object[]} corr.htmlResults
 * @param {object[]} corr.jsResults
 */
function onWebCorrectionSuccess(corr) {
    console.clear();
    console.log(JSON.stringify(corr, null, 2));

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

        for (let i = 0; i < corr.htmlResults.length; i = i + 3) {
            let secondToRender = corr.htmlResults[i + 1];
            let thirdToRender = corr.htmlResults[i + 2];
            html += `
<div class="row">
    <div class="col-md-4">${renderHtmlResult(corr.htmlResults[i])}</div>
    <div class="col-md-4">${secondToRender === undefined ? '' : renderHtmlResult(secondToRender)}</div>
    <div class="col-md-4">${thirdToRender === undefined ? '' : renderHtmlResult(thirdToRender)}</div>
</div>`.trim();
        }

        for (let result of corr.jsResults) {
            html += renderJsResult(result);
        }
    }

    $('#correction').html(html);
    $('#testButton').prop('disabled', false);
}

/**
 *
 * @param jqXHR {{responseText: string, responseJSON: string}}
 */
function onWebCorrectionError(jqXHR) {
    console.error(jqXHR.responseText);

    console.error(jqXHR.responseJSON);

    // $('#correction').html('<div class="alert alert-danger">' + jqXHR.responseText + '</div>');
    $('#testButton').prop('disabled', false);
}

function testSol() {
    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.exes.idPartExes.WebController.correctLive($('#exerciseId').val()).url;
    let part = $('#part').val();

    let dataToSend = {
        part,
        solution: editor.getValue()
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
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

function updatePreview() {
    const toWrite = unescapeHTML(editor.getValue());

    const theIFrame = document.getElementById('preview').contentWindow.document;
    theIFrame.open();
    theIFrame.write(toWrite);
    theIFrame.close();

}