function extractParameters() {
  const inputs = document.getElementsByTagName("input");
  let parameters = "count=" + inputs.length;
  for(let i = 0; i < inputs.length; i++) {
    parameters += "&inp" + i + "=" + inputs[i].value;
  }
  return parameters;
}

function processCorrection(correction) {
   const results = JSON.parse(correction);
   for(let i = 0; i < results.length; i++) {
    const correct = results[i] === "COMPLETE";
    
    const inp = document.getElementById("inp" + (i + 1));
    
    inp.parentNode.className = "form-group has-" + (correct ? "success" : "error") + " has-feedback";
  
    const newNode = document.createElement("span");
    newNode.className = "glyphicon glyphicon-" + (correct ?  "ok" : "warning-sign") + " form-control-feedback";

    const oldSign = inp.parentNode.getElementsByTagName("span");
    console.log(oldSign[0]);
    if(oldSign[0] == null) {
      inp.parentNode.appendChild(newNode);
    } else {
      oldSign[0].replaceWith(newNode);
    }
   }
 }