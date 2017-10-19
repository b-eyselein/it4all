function changeFontsize(value) {
  let fontsizeElement = document.getElementById('fontsize');
  let fontsize = parseInt(fontsizeElement.innerHTML) + value;
  document.getElementById('editor').style.fontSize = fontsize + 'px';
  fontsizeElement.innerHTML = fontsize;
}

function initEditor() {
  document.getElementById('editor').style.fontSize = '16px';
  editor = ace.edit('editor');

  editor.on('change', updateHiddenTextarea);

  editor.setTheme('ace/theme/eclipse');
  editor.getSession().setMode('ace/mode/' + theMode);
  editor.getSession().setTabSize(2);
  editor.getSession().setUseSoftTabs(true);
  editor.getSession().setUseWrapMode(true);
  editor.setOptions({
    minLines: theMinLines,
    maxLines: theMaxLines
  });
}

function updateHiddenTextarea() {
  $('#learnerSolution').val(editor.getValue());
}

function toParam(input) {
  return input.id + '=' + encodeURIComponent(input.value);
}

function paramFilter(input, element) {
  return element.id && element.value;
}

function extractParameters() {
  let inputs = $('form input, form textarea').filter(paramFilter);
  return $.map(inputs, toParam).join('&');
}

function testTheSolution(theUrl) {
  $('#testButton').prop('disabled', true);
  $.ajax({
    type: 'PUT',
    url: theUrl,
    data: extractParameters(),
    // FIXME: dataType: 'json',
    async: true,
    success: function (correction) {
      $('#correction').html(correction);
      $('#testButton').prop('disabled', false);
    },
    error: function (jqXHR, error, errorThrown) {
      $('#correction').html('<div class=\'alert alert-danger\'>' + jqXHR.responseJSON + '</div>');
      $('#testButton').prop('disabled', false);
    }
  });
}

function updatePreview() {
  let toWrite = unescapeHTML(editor.getValue());

  let theIFrame = document.getElementById('preview').contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

$(document).ready(function () {
  initEditor();
  updateHiddenTextarea();

  if (theUpdatePrev) {
    editor.on('change', updatePreview);
    updatePreview();
  }
});
