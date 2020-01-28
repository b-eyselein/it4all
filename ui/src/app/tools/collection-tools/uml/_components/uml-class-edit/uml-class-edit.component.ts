import {Component, Input} from '@angular/core';
import {MyJointClass} from '../../_model/joint-class-diag-elements';
import {UmlClassAttribute} from '../../_model/my-uml-interfaces';

@Component({
  selector: 'it4all-uml-class-edit',
  templateUrl: './uml-class-edit.component.html'
})
export class UmlClassEditComponent {

  readonly visibilities = ['+', '-', '#', '~'];
  readonly umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

  @Input() editedClass: MyJointClass;

  removeAttribute(attr: UmlClassAttribute): void {
    const newAttributes = this.editedClass.getAttributes().filter((a) => a !== attr);
    this.editedClass.setAttributes(newAttributes);

    console.info(this.editedClass.getAttributes());
  }

}
