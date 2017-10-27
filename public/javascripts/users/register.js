function feedbackAndSpans(obj, name, success) {
    obj.parent().parent().prop('class', 'form-group has-' + (success ? 'success' : 'error') + ' has-feedback');
    $('#span_' + name).prop('class', 'glyphicon glyphicon-' + (success ? "ok" : "remove") + ' form-control-feedback');
    $('#help_' + name).toggleClass('hidden', success);
}

function checkPws() {
    var firstPwOk = pw1.val().length >= 8;
    feedbackAndSpans(pw1, pw1Val, firstPwOk);
    return firstPwOk && checkSecondPw();
}

function checkSecondPw() {
    var secondPwOk = pw1.val() === pw2.val();
    feedbackAndSpans(pw2, pw2Val, secondPwOk);
    return secondPwOk;
}

function testFields() {
    return checkPws() && !$('#' + nameVal).data("taken");
}

/**
 * @param {{userexists: boolean, username: string}} result
 */
function nameUrl(result) {
    var nameField = $('#' + nameVal);
    nameField.prop('title', 'Der Nutzername "' + result.username + '" ist ' + (result.userexists ? 'bereits' : 'noch nicht') + ' registriert!');

    feedbackAndSpans(nameField, nameVal, !result.userexists);
    nameField.data("taken", result.userexists);
}

function checkUserName(checkUrl) {
    $.ajax({
        url: checkUrl,
        method: 'PUT',
        data: nameVal + "=" + $('#' + nameVal).val(),
        success: nameUrl,
        error: function (jsxhr) {
            console.log(jsxhr)
        }
    });
}
