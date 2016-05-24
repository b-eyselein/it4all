function processCorrection(correction) {
  try {
    var newResults = JSON.parse(correction);

    var numOfSuccessfulResults = 0;
	
    var resultsContainer = document.getElementById("element_result_container");
	// remove previous messages
	while (resultsContainer.firstChild) {
	  resultsContainer.removeChild(resultsContainer.firstChild);
	}
	
    for(i = 0; i < newResults.length; i++) {
      handleResult(newResults[i], i);
      if(newResults[i].success === "COMPLETE") {
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
  if(result.success === "COMPLETE") {
    resultDiv.className = "panel panel-success";
  } else if(result.success === "PARTIALLY") {
    resultDiv.className = "panel panel-warning";
  } else if(result.success === "NONE") {
    resultDiv.className = "panel panel-danger";
  }
  var panelHeading = document.createElement("div");
  panelHeading.className = "panel-heading";
  panelHeading.setAttribute("data-toggle", "collapse");
  panelHeading.href = "result_body_" + resultIndex;
  panelHeading.appendChild(new Text(result.title));
  resultDiv.appendChild(panelHeading);
  
  if(result.message !== "") {
    // create button
	var pullButton = document.createElement("button");
	pullButton.className = "glyphicon glyphicon-chevron-down pull-right";
	pullButton.setAttribute("data-toggle", "collapse");
	pullButton.setAttribute("data-target", "result_body_" + resultIndex);
	panelHeading.appendChild(pullButton);
  
    // create message div
	var panelCollapse = document.createElement("div");
	panelCollapse.className = "panel-collapse"; // collapse in";
	// panelCollapse.id = "result_body_" + resultIndex;
	var panelBody = document.createElement("div");
	panelBody.className = "collapse";
	panelBody.id = "result_body_" + resultIndex;
	panelBody.appendChild(new Text(result.message));
	panelCollapse.appendChild(panelBody);
	resultDiv.appendChild(panelCollapse);
	
	  if(result.success === "COMPLETE") {
        // panelCollapse.setAttribute("data-toggle", "collapse");
	  } else if(result.success === "PARTIALLY") {
		// panelCollapse.collapse('show');
	  } else if(result.success === "NONE") {
		// panelCollapse.collapse('show');
	  }
  }
  
  resultsContainer.appendChild(resultDiv);
}
