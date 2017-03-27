const LEARNER_SOLUTION_VALUE = "learnerSolution";
const CORRECTION_FIELD_VALUE = "#correction";

function changeFontsize(value) {
  var fontsizeElement = document.getElementById('fontsize');
  var fontsize = parseInt(fontsizeElement.innerHTML) + value;
  document.getElementById('editor').style.fontSize = fontsize + 'px';
  fontsizeElement.innerHTML = fontsize;
}

function initEditor(theMode, theMinLines, theMaxLines) {
  document.getElementById('editor').style.fontSize = '16px';
  editor = ace.edit("editor");
  editor.setTheme("ace/theme/eclipse");
  editor.getSession().setMode("ace/mode/" + theMode);
  editor.getSession().setTabSize(2);
  editor.getSession().setUseSoftTabs(true);
  editor.getSession().setUseWrapMode(true);
  editor.setOptions({
    minLines: theMinLines,
    maxLines: theMaxLines
  });
}

function processCorrection(correction) {
  document.getElementById(CORRECTION_FIELD_VALUE).innerHTML = correction;
}

function extractParameters() {
  return LEARNER_SOLUTION_VALUE + "=" + encodeURIComponent(editor.getValue());
}

function prepareFormForSubmitting() {
  document.getElementById(LEARNER_SOLUTION_VALUE).value = editor.getValue();
}

function testTheSolution(theUrl) {
  $.ajax({
    type: 'PUT',
    url: theUrl,
    data: extractParameters(),
    async: true,
    success: function(response) {
      $(CORRECTION_FIELD_VALUE).html(response);
    }
  });
}

function updatePreview() {
  var toWrite = unescapeHTML(editor.getValue());
  
  var theIFrame = document.getElementById("preview").contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}
