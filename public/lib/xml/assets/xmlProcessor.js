function processCorrection(correction) {
  try {
    var newResults = JSON.parse(correction);

    var numOfSuccessfulResults = 0;
	
    var resultsContainer = document.getElementById("element_result_container");
	resultsContainer.innerHtml = ""; // remove previous message
	
    for(i = 0; i < newResults.length; i++) {
      handleResult(newResults[i]);
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

function handleResult(result) {
  var resultsContainer = document.getElementById("element_result_container");
  
  var resultDiv = document.createElement("div");
  resultDiv.className = "panel panel-default";
  var panelHeading = document.createElement("div");
  panelHeading.className = "panel-heading";
  panelHeading.setAttribute("data-toggle", "collapse");
  panelHeading.setAttribute("href", "result_body_" + resultsContainer.length); // href
  var pullButton = document.createElement("span");
  pullButton.className = "glyphicon glyphicon-chevron-down pull-right";
  panelHeading.textContent = result.title;
  panelHeading.appendChild(pullButton); // !!!
  
  var panelCollapse = document.createElement("div");
  panelCollapse.className = "panel-collapse collapse";
  panelCollapse.id = "result_body_" + resultsContainer.length;
  var panelBody = document.createElement("div");
  panelBody.className = "panel-body";
  panelBody.appendChild(new Text(result.message));
  panelCollapse.appendChild(panelBody);
  
  resultDiv.appendChild(panelHeading);
  resultDiv.appendChild(panelCollapse);
  
  if(result.success === "COMPLETE") {
    resultDiv.className = "panel panel-success";
    // panelCollapse.collapse('hide');
  } else if(result.success === "PARTIALLY") {
    resultDiv.className = "panel panel-warning";
    // panelCollapse.collapse('show');
  } else if(result.success === "NONE") {
    resultDiv.className = "panel panel-danger";
    // panelCollapse.collapse('show');
  } else {
    alert("Es gab einen Fehler!");
  }
  
  resultsContainer.appendChild(resultDiv);
}
