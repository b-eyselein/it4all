function extractParameters() {
  var parameters = "";
  var inputs = document.getElementById("valueDiv").getElementsByTagName("input");
  for(var i = 0; i < inputs.length; i++) {
    if(parameters !== "") {
      parameters += "&";
    }
    parameters += inputs[i].name + "=" + inputs[i].value;
  }
  parameters += "&learnerSolution=" + document.getElementById("learnerSolution").value;
  return parameters;
}

function testTheSolution(url) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = extractParameters();
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}

function processCorrection(correctionAsJson) {
  var completeCorrection = JSON.parse(correctionAsJson);
  var correctionArray = completeCorrection.solutions;
  
  for(var i = 0; i < correctionArray.length; i++) {
    var correction = correctionArray[i];
    var solutionCell = document.getElementById(correction.assignmentsForJson);
    solutionCell.className = "text-center " + correction.color;
    solutionCell.innerHTML = "<span class=\"text-" + correction.color + "\">" + correction.learnerValue + "</span>";
  }
  
}
