/**
 *
 * @param {Object[]} response
 * @param {number} response.id
 * @param {string} response.correctness
 * @param {string} response.explanation
 */
function onAjaxSuccess(response) {
    console.log(response);
    for (result of response) {
        const span = $('#span_' + result.id);
        const inputParent = $('#' + result.id).parent();
        switch (result.correctness) {
            case 'SUCCESSFUL_MATCH':
                span.removeClass('glyphicon-warning-sign glyphicon-remove').addClass('glyphicon glyphicon-ok form-control-feedback');
                inputParent.removeClass('has-warning has-error has-feedback').addClass('has-success has-feedback');
                break;
            case 'PARTIAL_MATCH':
                span.removeClass('glyphicon-ok glyphicon-remove').addClass('glyphicon glyphicon-warning-sign form-control-feedback');
                inputParent.removeClass('has-success has-error has-feedback').addClass('has-warning has-feedback');
                break;
            case 'UNSUCCESSFUL_MATCH':
                span.removeClass('glyphicon-ok glyphicon-warning-sign').addClass('glyphicon glyphicon-remove form-control-feedback');
                inputParent.removeClass('has-success has-error has-feedback').addClass('has-error has-feedback');
                break;
            default:
                break;
        }
        inputParent.prop('title', result.explanation);
    }
}

function onAjaxError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol(theUrl) {
    let answers = $('input').map((index, elem) => {
        return {id: index + 1, value: elem.value};
    }).get();


    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(answers),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

// function processCorrection(correction) {
//     const results = JSON.parse(correction);
//     for (let i = 0; i < results.length; i++) {
//         const correct = results[i] === 'COMPLETE';
//
//         const inp = document.getElementById('inp' + (i + 1));
//
//         inp.parentNode.className = 'form-group has-' + (correct ? 'success' : 'error') + ' has-feedback';
//
//         const newNode = document.createElement('span');
//         newNode.className = 'glyphicon glyphicon-' + (correct ? 'ok' : 'warning-sign') + ' form-control-feedback';
//
//         const oldSign = inp.parentNode.getElementsByTagName('span');
//         console.log(oldSign[0]);
//         if (oldSign[0] === null) {
//             inp.parentNode.appendChild(newNode);
//         } else {
//             oldSign[0].replaceWith(newNode);
//         }
//     }
// }