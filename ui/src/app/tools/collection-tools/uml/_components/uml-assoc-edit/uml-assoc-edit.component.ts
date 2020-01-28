import {Component, Input} from '@angular/core';
import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-assoc-edit',
  templateUrl: './uml-assoc-edit.component.html'
})
export class UmlAssocEditComponent {

  @Input() editedAssociation: joint.shapes.uml.Association;

}
