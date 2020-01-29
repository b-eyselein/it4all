import {Component, Input} from '@angular/core';
import {MyJointClass} from '../../_model/joint-class-diag-elements';
import {IUmlAttribute} from '../../uml-interfaces';

@Component({
  selector: 'it4all-uml-class-edit',
  templateUrl: './uml-class-edit.component.html'
})
export class UmlClassEditComponent {

  readonly visibilities = ['+', '-', '#', '~'];
  readonly umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

  @Input() editedClass: MyJointClass;

  removeAttribute(attr: IUmlAttribute): void {
    const newAttributes = this.editedClass.getAttributes().filter((a) => a !== attr);
    this.editedClass.setAttributes(newAttributes);

    console.info(this.editedClass.getAttributes());
  }

}
