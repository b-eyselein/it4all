import {Component, Input, OnInit} from '@angular/core';
import {ExerciseTag} from '../../../../_interfaces/tool';

@Component({
  selector: 'it4all-tag',
  template: `<span class="tag is-info" [title]="tag.title">{{tag.label}}</span>&nbsp;`
})
export class TagComponent implements OnInit {

  @Input() tag: ExerciseTag;

  constructor() {
  }

  ngOnInit() {
  }

}
