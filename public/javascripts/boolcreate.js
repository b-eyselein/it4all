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

function processCorrection(correction) {
  alert(correction);
  
//  var jsonObject = JSON.parse(correction);
}
