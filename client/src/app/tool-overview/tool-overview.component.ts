import {Component, OnInit} from '@angular/core';
import {collectionTools} from '../tools/collection-tools/collection-tools-list';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {Tool} from '../_interfaces/tool';

@Component({templateUrl: './tool-overview.component.html'})
export class ToolOverviewComponent implements OnInit {
  randomTools: Tool[] = randomTools;
  collectionTools: Tool[] = collectionTools;

  ngOnInit(): void {
    console.info(sessionStorage.getItem('jwt'));
  }

}
