function extractParameters() {
  const inputs = document.getElementsByTagName("input");
  let parameters = "count=" + inputs.length;
  for(let i = 0; i < inputs.length; i++) {
    parameters += "&inp" + i + "=" + inputs[i].value;
  }
  return parameters;
}

function processCorrection(correction) {
  // console.log(correction);
}
  
function test() {
  var inputs = document.getElementsByTagName("input");
  for(const inp of inputs) {
    const correct = inp.getAttribute("data-sol") === inp.value;
     
    inp.parentNode.className = "form-group has-" + (correct ? "success" : "error") + " has-feedback";
  
    const newNode = document.createElement("span");
    newNode.className = "glyphicon glyphicon-" + (correct ?  "ok" : "warning-sign") + " form-control-feedback";

    const oldSign = inp.parentNode.getElementsByTagName("span");
    if(oldSign[0] == null) {
      inp.parentNode.appendChild(newNode);
    } else {
      oldSign[0].replaceWith(newNode);
    }
  }
 }