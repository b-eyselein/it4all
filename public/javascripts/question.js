// In newQuestion / editQuestion
const MAX_ANSWERS = 8;
const MAX_START = 5

function onAnswerNumChange() {
  var numOfActiveAnswers = parseInt(document.getElementById("numOfAnswers").value);
  for(var i = 0; i < MAX_ANSWERS ; i++) {
    var input = document.getElementById(i);
    var answerActive = i < numOfActiveAnswers;

    // Activate inputs for answers 1 to numOfAnswers
    input.readOnly = answerActive ? "" : "readonly";
    input.required = answerActive;
    input.className = "form-control" + (answerActive ? "" : " disabled");
  }
}

function changeCorrectness(number, value, button) {
  // TODO: set correctness...
  document.getElementById("correctness_" + number).value = value;
  
  for(var otherButton of button.parentNode.children) {
    otherButton.className = "btn btn-default";
  }
  button.className = "btn btn-primary";
}

// In questionResult
function setStars(num) {
  for(var i = 1; i <= MAX_START; i++) {
    var newClassName = i <= num ? "glyphicon glyphicon-star" : "glyphicon glyphicon-star-empty";
    document.getElementById("star" + i).className = newClassName;
  }
  document.getElementById("stars").value = num;
}
