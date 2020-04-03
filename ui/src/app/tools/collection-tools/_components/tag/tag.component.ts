import {Component, Input, OnInit} from '@angular/core';
import {ExTag} from "../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-tag',
  template: `<span class="tag is-info" [title]="tag.title">{{tag.abbreviation}}</span>&nbsp;`
})
export class TagComponent implements OnInit {

  @Input() tag: ExTag;

  constructor() {
  }

  ngOnInit() {
  }

}
