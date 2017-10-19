// In newQuestion / editQuestion
const MAX_ANSWERS = 8;
const MAX_START = 5;

function onAnswerNumChange() {
  let numOfActiveAnswers = parseInt(document.getElementById('numOfAnswers').value);
  for (let i = 0; i < MAX_ANSWERS; i++) {
    let input = document.getElementById(i);
    let answerActive = i < numOfActiveAnswers;

    // Activate inputs for answers 1 to numOfAnswers
    input.readOnly = answerActive ? '' : 'readonly';
    input.required = answerActive;
    input.className = 'form-control' + (answerActive ? '' : ' disabled');
  }
}

function changeCorrectness(number, value, button) {
  // TODO: set correctness...
  document.getElementById('correctness_' + number).value = value;

  for (let otherButton of button.parentNode.children) {
    otherButton.className = 'btn btn-default';
  }
  button.className = 'btn btn-primary';
}

// In questionResult
function setStars(num) {
  for (let i = 1; i <= MAX_START; i++) {
    document.getElementById('star' + i).className = i <= num ? 'glyphicon glyphicon-star' : 'glyphicon glyphicon-star-empty';
  }
  document.getElementById('stars').value = num;
}
