function processCorrection(correction) {
  document.getElementById('correction').innerHTML = correction;
}

function extractParameters() {
  return 'learnerSolution=' + encodeURIComponent(editor.getValue());
}

function prepareFormForSubmitting() {
  document.getElementById('editorContent').value = editor.getValue();
}
