import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import * as joint from 'jointjs';
import {MyJointClass} from '../../_model/joint-class-diag-elements';

@Component({
  selector: 'it4all-uml-assoc-edit',
  templateUrl: './uml-assoc-edit.component.html'
})
export class UmlAssocEditComponent implements OnChanges {

  @Input() editedAssociation: joint.shapes.uml.Association;

  @Output() cancel = new EventEmitter<void>();

  firstEnd: MyJointClass;
  firstMult: string;

  secondEnd: MyJointClass;
  secondMult: string;

  ngOnChanges(changes: SimpleChanges): void {
    const labels = this.editedAssociation.labels();

    this.firstEnd = this.editedAssociation.getSourceCell() as MyJointClass;
    this.firstMult = labels[0].attrs.text.text;

    this.secondEnd = this.editedAssociation.getTargetCell() as MyJointClass;
    this.secondMult = labels[1].attrs.text.text;
  }

  updateAssoc(firstMultValue: string, secondMultValue: string): void {
    this.editedAssociation.label(0, {position: 25, attrs: {text: {text: firstMultValue}}});
    this.editedAssociation.label(1, {position: -25, attrs: {text: {text: secondMultValue}}});
  }

}
