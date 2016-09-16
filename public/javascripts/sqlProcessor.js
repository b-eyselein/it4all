function testTheSolution(url) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText);
    }
  };
  
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(getParameters());
}

function getParameters() {
  var editorContent = editor.getValue();
  var parameters = "editorContent=" + encodeURIComponent(editorContent);
  
  var feedbackLevel = document.getElementById("feedbackLevel");
  var realLevel = feedbackLevel.options[feedbackLevel.selectedIndex].value;
  parameters += "&feedbackLevel=" + realLevel;
  return parameters;
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  
  var toAdd = "";
  for(var i = 0; i < parsedCorr.length; i++)
    toAdd += parsedCorr[i].asHtml;
  
  document.getElementById("resultPanel").hidden = false;
  document.getElementById("resultDiv").innerHTML = toAdd;
}
