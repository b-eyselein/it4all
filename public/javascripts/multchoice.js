const
MARKED_CLASS_NAME = "btn btn-primary btn-block";
const
UNMARKED_CLASS_NAME = "btn btn-default btn-block";

var solutions = {
  "1": [0, 2],
  "2": [3, 5]
};

function mark(button) {
  if(button.className == MARKED_CLASS_NAME) {
    button.className = UNMARKED_CLASS_NAME;
    button.dataset.selected = 0;
  } else {
    button.className = MARKED_CLASS_NAME;
    button.dataset.selected = 1;
  }
}

function control(id, answerCount) {
  var sols = solutions[id];
  var answers = [];
  for(var i = 0; i < answerCount; i++) {
    var buttonId = id + "_" + i ;
    if(document.getElementById(buttonId).dataset.selected == 1) {
      answers.push(i);
    }
  }
  console.log(sols + " <--> " + answers);
}
