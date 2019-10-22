import {Component} from '@angular/core';
import {collectionTools} from '../tools/collection-tools/collection-tools-list';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {Tool} from '../_interfaces/tool';

@Component({templateUrl: './tool-overview.component.html'})
export class ToolOverviewComponent {
  randomTools: Tool[] = randomTools;
  collectionTools: Tool[] = collectionTools;
}
