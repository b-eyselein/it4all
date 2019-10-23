import {Component, OnInit} from '@angular/core';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {Tool} from '../../_interfaces/tool';

@Component({templateUrl: './admin-index.component.html'})
export class AdminIndexComponent implements OnInit {

  collTools: Tool[] = collectionTools;

  constructor() {
  }

  ngOnInit() {
  }

}
