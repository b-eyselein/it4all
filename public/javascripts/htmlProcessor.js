function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function extractParameters() {
  return "editorContent=" + encodeURIComponent(editor.getValue());
}

function processCorrection(correction) {
  var newCorrection = JSON.parse(correction);
  
  var correctionDiv = document.getElementById("correction");
  correctionDiv.innerHTML = newCorrection.asHtml;
  
  var commitButton = document.getElementById("commit");
  commitButton.disabled = false;
  if(newCorrection.success == "COMPLETE") {
    commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
    commitButton.className = "btn btn-success";
  } else {
    commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben, haben aber nicht alles richtig.";
    commitButton.className = "btn btn-warning";
  }
}
