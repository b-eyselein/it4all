const prefUrl = $('#commit').data('url');

function savePreferences(value) {
  var inp = $('#' + value);
  $.ajax({
    url: prefUrl,
    type: 'PUT',
    data: value + '=' + inp.val(),
    success: function (result) {
      inp.data('std', result.todo);
      update(value);
    }
  });
}

function update(value) {
  var inp = $('#' + value);
  $('#btn_' + value).prop('disabled', inp.val() === inp.data('std'));
}