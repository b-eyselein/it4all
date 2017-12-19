function getAllSpans(cap) {
    let allSpans = [].slice.call(document.getElementById('textDiv').getElementsByTagName('span'));
    return allSpans.filter(function (span) {
        return span.dataset.baseform === cap;
    });
}

function getBaseformInner(cap, baseformInput) {
    return cap + '(--> "' + baseformInput.value + '")';
}

function radioChange(radio) {
    let cap = radio.name;

    let className = '';
    let toDisable = true;
    let newInner = cap;

    let baseformInput = document.getElementById(cap + '_baseform');

    switch (radio.value) {
        case 'ignore':
            break;
        case 'baseform':
            toDisable = false;
            className = 'non-marked';
            newInner = getBaseformInner(cap, baseformInput);
            break;
        case 'none':
        default:
            className = 'non-marked';
            break;
    }

    baseformInput.disabled = toDisable;

    // Convert HtmlCollection to array...
    for (let span of getAllSpans(cap)) {
        span.className = className;
        span.innerHTML = newInner;
    }
}

function baseformChange(baseformInput) {
    let cap = baseformInput.dataset.cap;
    for (let span of getAllSpans(cap)) {
        span.innerHTML = getBaseformInner(cap, baseformInput);
    }
}

function onCheckSolutionSuccess(response) {
    let solutionvalidDiv = $('#solutionValid');
    switch (response) {
        case 'ok':
            solutionvalidDiv.attr('class', 'alert alert-success');
            solutionvalidDiv.html('<span class="glyphicon glyphicon-ok"></span> L&ouml;sung entspricht dem JSON-Schema.');
            break;
        case 'error':
            solutionvalidDiv.attr('class', 'alert alert-danger');
            solutionvalidDiv.html('<span class="glyphicon glyphicon-remove"></span> Ihr Dokument enth&auml;lt einen Fehler!');
            break;
        default:
            solutionvalidDiv.attr('class', 'alert alert-danger');
            solutionvalidDiv.html('<span class="glyphicon glyphicon-remove"></span> Ihr Dokument enth&auml;lt folgende Fehler:<pre>'
                + response + '</pre>');
            break;
    }
}

function checkSolutionWithSchema() {
    let solution = encodeURIComponent(editor.getValue());
    $.ajax({
        type: 'PUT',
        url: url,
        data: 'solution=' + solution,
        async: true,
        success: onCheckSolutionSuccess
    });
}