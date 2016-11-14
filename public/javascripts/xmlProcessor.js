function processCorrection(correction) {
  var newResults = JSON.parse(correction);
  
  document.getElementById("correction").innerHTML = newResults.asHtml;
  
  var commitButton = document.getElementById("commit");
  commitButton.disabled = false;
  if(newResults.success == "COMPLETE") {
    commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
    commitButton.className = "btn btn-success";
  } else {
    commitButton.title = "Sie können ihre Lösung abgeben, haben aber nicht alles richtig.";
    commitButton.className = "btn btn-warning";
  }
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function extractParameters() {
  return "editorContent=" + encodeURIComponent(editor.getValue());
}
