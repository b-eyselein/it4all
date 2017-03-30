const
MARKED_CLASS_NAME = "btn btn-primary btn-block";

const
UNMARKED_CLASS_NAME = "btn btn-default btn-block";

function mark(button) {
  if(button.className == MARKED_CLASS_NAME) {
    button.className = UNMARKED_CLASS_NAME;
    button.dataset.selected = 0;
  } else {
    button.className = MARKED_CLASS_NAME;
    button.dataset.selected = 1;
  }
}

function markSingle(button) {
  // Unmark all buttons
  var numOfAnswers = document.getElementById("question").dataset.answers;
  for(var i = 0; i < numOfAnswers; i++) {
    var buttonToUnmark = getAnswerButton(i);
    buttonToUnmark.className = UNMARKED_CLASS_NAME;
    buttonToUnmark.dataset.selected = 0;
  }
  
  // Mark button
  button.className = MARKED_CLASS_NAME;
  button.dataset.selected = 1;
}

function extractSelectedAnswers(id, numOfAnswers) {
  var selectedAnswers = [];
  
  for(var i = 0; i < numOfAnswers; i++) {
    if(getAnswerButton(i).dataset.selected == 1) {
      selectedAnswers.push(i);
    }
  }
  
  return selectedAnswers;
}

function getAnswerButton(num) {
  return document.getElementById("ans_" + num);
}

function colorButton(button, color) {
  button.className = "btn btn-block " + color;
}

function control(theUrl, id) {
  var questionDiv = document.getElementById("question");
  var numOfAnswers = questionDiv.dataset.answers;
  
  var selectedAnswers = extractSelectedAnswers(id, numOfAnswers);
  
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
      for(var i = 0; i < numOfAnswers; i++) {
        getAnswerButton(i).onclick = "";
      }
      // Deactivate control functionality
      var correctionButton = document.getElementById("correct_" + id);
      correctionButton.onclick = "";
      correctionButton.className = "btn btn-block btn-default disabled";
      
      // Mark correct, missing and wrong answers
      for(var corr of response.correct) {
        var button = getAnswerButton(corr);
        colorButton(button, "btn-success");
        button.title = "Diese Antwort war korrekt";
      }
      for(var miss of response.missing) {
        var button = getAnswerButton(miss);
        colorButton(button, "btn-warning");
        button.title = "Diese Antwort war nicht ausgewählt, aber korrekt.";
      }
      for(var wro of response.wrong) {
        var button = getAnswerButton(wro);
        colorButton(button, "btn-danger");
        button.title = "Diese Antwort war falsch.";
      }
    }
  });
}
