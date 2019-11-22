import {Component} from '@angular/core';
import {collectionTools} from '../../tools/collection-tools/collection-tools-list';
import {CollectionTool} from '../../_interfaces/tool';

@Component({templateUrl: './admin-index.component.html'})
export class AdminIndexComponent {

  collTools: CollectionTool<any>[] = collectionTools;

  constructor() {
  }

}
