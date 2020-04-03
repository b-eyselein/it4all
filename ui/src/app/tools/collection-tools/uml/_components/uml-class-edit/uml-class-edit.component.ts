import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MyJointClass} from '../../_model/joint-class-diag-elements';
import {UmlAttribute} from "../../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-uml-class-edit',
  templateUrl: './uml-class-edit.component.html'
})
export class UmlClassEditComponent {

  readonly visibilities = ['+', '-', '#', '~'];
  readonly umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

  @Input() editedClass: MyJointClass;
  @Output() cancel = new EventEmitter<void>();

  removeAttribute(attr: UmlAttribute): void {
    const newAttributes = this.editedClass.getAttributes().filter((a) => a !== attr);

    this.editedClass.setAttributes(newAttributes);
  }

}
