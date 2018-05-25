import * as $ from 'jquery';

let pw1: JQuery, pw2: JQuery;

$(() => {
    pw1 = $('#passwort');
    pw2 = $('#passwort_wieder');

    $('form').on('submit', testFields);
});

function feedbackAndSpans(obj, name, success) {
    obj.parent().parent().prop('class', 'form-group has-' + (success ? 'success' : 'error') + ' has-feedback');
    $('#span_' + name).prop('class', 'glyphicon glyphicon-' + (success ? 'ok' : 'remove') + ' form-control-feedback');
    $('#help_' + name).toggleClass('hidden', success);
}

function checkPws() {
    const firstPwOk = (pw1.val() as string).length >= 8;
    feedbackAndSpans(pw1, 'passwort', firstPwOk);
    return firstPwOk && checkSecondPw();
}

function checkSecondPw() {
    const secondPwOk = pw1.val() === pw2.val();
    feedbackAndSpans(pw2, 'passwort_wieder', secondPwOk);
    return secondPwOk;
}

function testFields() {
    return checkPws() && !$('#name').data('taken');
}

/**
 * @param {{userexists: boolean, username: string}} result
 */
function nameUrl(result) {
    const nameField = $('#name');
    nameField.prop('title', 'Der Nutzername "' + result.username + '" ist ' + (result.userexists ? 'bereits' : 'noch nicht') + ' registriert!');

    feedbackAndSpans(nameField, 'name', !result.userexists);
    nameField.data('taken', result.userexists);
}

function checkUserName(checkUrl) {
    $.ajax({
        url: checkUrl,
        method: 'PUT',
        data: 'name=' + $('#name').val(),
        success: nameUrl,
        error: function (jqXHR): void {
            console.log(jqXHR)
        }
    });
}
