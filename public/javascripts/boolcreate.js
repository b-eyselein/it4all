function extractParameters() {
  var parameters = "";
  var inputs = document.getElementById("valueDiv").getElementsByTagName("input");
  for(i = 0; i < inputs.length; i++) {
    if(parameters !== "") {
      parameters += "&";
    }
    parameters += inputs[i].name + "=" + inputs[i].value;
  }
  parameters += "&learnerSolution=" + document.getElementById("learnerSolution").value;
  return parameters;
}

function processCorrection(correctionAsJson) {
  var completeCorrection = JSON.parse(correctionAsJson);
  var correctionArray = completeCorrection.solutions;
  
  for(i = 0; i < correctionArray.length; i++) {
    var correction = correctionArray[i];
    var solutionCell = document.getElementById(correction.assignmentsForJson);
    solutionCell.innerHTML = "<span class=\"" + correction.color + "\">" + correction.learnerValue + "</span>";
  }
  
}
