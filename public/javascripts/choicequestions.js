const
MARKED_CLASS_NAME = "btn btn-block btn-primary";

const
UNMARKED_CLASS_NAME = "btn btn-block btn-default";

function mark(button) {
  button.className = (button.className == MARKED_CLASS_NAME) ? UNMARKED_CLASS_NAME : MARKED_CLASS_NAME;
  button.dataset.selected = (parseInt(button.dataset.selected) + 1) % 2;
}

function getAnswerButtons() {
  return document.getElementById("answerDiv").children;
}

function markSingle(button) {
  // Unmark all buttons
  for(var buttonToUnmark of getAnswerButtons()) {
    buttonToUnmark.className = UNMARKED_CLASS_NAME;
    buttonToUnmark.dataset.selected = 0;
  }
  
  // Mark button
  button.className = MARKED_CLASS_NAME;
  button.dataset.selected = 1;
}

function extractSelectedAnswers() {
  var selectedAnswers = [];

  for(var button of getAnswerButtons()) {
    if(button.dataset.selected == 1) {
      selectedAnswers.push(button.id);
    }
  }
  
  return selectedAnswers;
}

function getAnswerButton(num) {
  return document.getElementById(num);
}

function colorButton(button, color) {
  button.className = "btn btn-block btn-" + color;
}

function control(theUrl) {
  var selectedAnswers = extractSelectedAnswers();
  
  if(selectedAnswers.length == 0) {
    alert("Wählen Sie bitte eine Antwort aus!");
    return;
  }
  
  $.ajax({
    type: 'PUT',
    url: theUrl,
    data: "selected=" + JSON.stringify(selectedAnswers),
    async: true,
    success: function(response) {
      // Deactivate selection of answers
      for(var button of getAnswerButtons()) {
        button.onclick = "";
      }
      
      // Deactivate control functionality
      var correctionButton = document.getElementById("correct");
      correctionButton.onclick = "";
      correctionButton.className = "btn btn-block btn-default disabled";
      
      // TODO: Mark correct, missing and wrong answers
      for(var corr of response.correct) {
        var button = getAnswerButton(corr);
        colorButton(button, "success");
        button.title = "Diese Antwort war korrekt";
      }
      for(var miss of response.missing) {
        var button = getAnswerButton(miss);
        colorButton(button, "warning");
        button.title = "Diese Antwort war nicht ausgewählt, aber korrekt.";
      }
      for(var wro of response.wrong) {
        var button = getAnswerButton(wro);
        colorButton(button, "danger");
        button.title = "Diese Antwort war falsch.";
      }
    }
  });
}
