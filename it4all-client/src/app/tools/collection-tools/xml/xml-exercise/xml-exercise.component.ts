import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IXmlExerciseContent} from '../../../../_interfaces/models';

@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent implements OnInit {

  @Input() exercise: IExercise;

  exerciseContent: IXmlExerciseContent;

  constructor() {
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IXmlExerciseContent;
  }

}
