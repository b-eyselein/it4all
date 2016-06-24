function processCorrection(correction) {
  try {
    var newResults = JSON.parse(correction);
    
    var numOfSuccessfulResults = 0;
    
    var resultsContainer = document.getElementById("element_result_container");
    
    // remove previous messages
    while(resultsContainer.firstChild) {
      resultsContainer.removeChild(resultsContainer.firstChild);
    }
    
    for(i = 0; i < newResults.length; i++) {
      handleResult(newResults[i], i);
      if(newResults[i].errorType === "NONE") {
        numOfSuccessfulResults++;
      }
    }
    
    var commitButton = document.getElementById("commit");
    commitButton.disabled = false;
    if(numOfSuccessfulResults == newResults.length) {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
      commitButton.className = "btn btn-success";
    } else {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben, haben aber nicht alles richtig.";
      commitButton.className = "btn btn-warning";
    }
  } catch(err) {
    alert(err);
  }
}

function handleResult(result, resultIndex) {
  var resultsContainer = document.getElementById("element_result_container");
  
  var resultDiv = document.createElement("div");
  if(result.errorType === "NONE") {
    resultDiv.className = "panel panel-success";
  } else if(result.errorType === "WARNING") {
    resultDiv.className = "panel panel-warning";
  } else if(result.errorType === "FATALERROR" || result.errorType === "ERROR") {
    resultDiv.className = "panel panel-danger";
  }
  var panelHeading = document.createElement("div");
  panelHeading.className = "panel-heading";
  panelHeading.setAttribute("data-toggle", "collapse");
  panelHeading.href = "result_body_" + resultIndex;
  if(result.line <= 0) {
    panelHeading.appendChild(new Text(result.title));
  } else {
    panelHeading.appendChild(new Text("(Zeile:" + result.line + ") " + result.title));
  }
  resultDiv.appendChild(panelHeading);
  
  if(result.errorMessage !== "") {
    // create button
    var pullButton = document.createElement("button");
    pullButton.className = "glyphicon glyphicon-chevron-down pull-right";
    pullButton.setAttribute("data-toggle", "collapse");
    pullButton.setAttribute("href", "#result_body_" + resultIndex);
    panelHeading.appendChild(pullButton);
    
    // create message div
    if(result.errorMessage.startsWith("cvc-complex-type.2.4.")) {
      result.errorMessage = result.errorMessage.substring(24);
    }
    result.errorMessage = " " + result.errorMessage;
    
    var panelCollapse = document.createElement("div");
    if(result.errorType === "COMPLETE") {
      panelCollapse.className = "panel-collapse collapse";
    } else if(result.errorType === "WARNING") {
      panelCollapse.className = "panel-collapse collapse";
    } else if(result.errorType === "FATALERROR" || result.errorType === "ERROR") {
      panelCollapse.className = "panel-collapse collapse in";
    }
    panelCollapse.id = "result_body_" + resultIndex;
    var panelBody = document.createElement("h4");
    panelBody.appendChild(new Text(result.errorMessage));
    panelCollapse.appendChild(panelBody);
    resultDiv.appendChild(panelCollapse);
    
  }
  
  resultsContainer.appendChild(resultDiv);
}
