import {Component, OnInit} from '@angular/core';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {RandomTool} from '../tools/random-tools/random-tool';
import {CollectionToolFragment, ToolOverviewGQL, ToolOverviewQuery, ToolState} from '../_services/apollo_services';

@Component({templateUrl: './tool-overview.component.html'})
export class ToolOverviewComponent implements OnInit {

  randomTools: RandomTool[] = randomTools;

  toolOverviewQuery: ToolOverviewQuery;

  constructor(private toolOverviewGQL: ToolOverviewGQL) {
  }

  ngOnInit(): void {
    this.toolOverviewGQL
      .watch()
      .valueChanges
      .subscribe(({data}) => this.toolOverviewQuery = data);
  }

  isLive(toolState: ToolState): boolean {
    return toolState === ToolState.Live;
  }

  get collectionTools(): CollectionToolFragment[] {
    if (this.toolOverviewQuery) {
      return this.toolOverviewQuery.me.tools;
    } else {
      return [];
    }
  }

}
