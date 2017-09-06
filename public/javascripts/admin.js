function deleteExercise(theUrl) {
  $.ajax({
    type: 'DELETE',
    url: theUrl,
    success: updateAfterDelete
  });
}

function updateAfterDelete(result) {
  var tr = $('#tr1_' + result.id);
  tr.addClass('danger');
  tr.attr('title', 'Diese Aufgabe wurde erfolgreich gelöscht.');
  
  var delButton = $('#del_' + result.id);
  delButton.prop('disabled', true);
  delButton.attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
  
  var editButton = $('#edit_' + result.id);
  editButton.addClass('disabled');
  editButton.removeAttr('href');
  editButton.attr('title', 'Diese Aufgabe wurde bereits gelöscht!');
}

function updateStateChangeButton(id) {
  var select = $('#state_' + id);
  $('#scb_' + id).prop('disabled', select.val() === select.data('val'));
}

function changeState(theUrl, id) {
  $.ajax({
    type: 'PUT',
    url: theUrl,
    data: {
      state: $('#state_' + id).val()
    },
    success: function(result) {
      $('#scb_' + result.id).prop('disabled', true);
      $('#state_' + result.id).data('val', result.newState);
    }
  });
}
