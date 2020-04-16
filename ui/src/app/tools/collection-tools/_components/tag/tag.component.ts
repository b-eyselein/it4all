import {Component, Input, OnInit} from '@angular/core';
import {TopicFragment} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-tag',
  template: `<span class="tag is-info" [title]="topic.title">{{topic.abbreviation}}</span>&nbsp;`
})
export class TagComponent implements OnInit {

  @Input() topic: TopicFragment;

  constructor() {
  }

  ngOnInit() {
  }

}
