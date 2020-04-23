import {Component, OnInit} from '@angular/core';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {RandomTool} from '../_interfaces/tool';
import {ToolOverviewGQL, ToolOverviewQuery} from "../_services/apollo_services";
import {ToolState} from "../_interfaces/graphql-types";

@Component({templateUrl: './tool-overview.component.html'})
export class ToolOverviewComponent implements OnInit {

  randomTools: RandomTool[] = randomTools;

  toolOverviewQuery: ToolOverviewQuery;

  constructor(private toolOverviewGQL: ToolOverviewGQL) {
  }

  ngOnInit(): void {
    this.toolOverviewGQL
      .watch({})
      .valueChanges
      .subscribe(({data}) => this.toolOverviewQuery = data);
  }

  isLive(toolState: ToolState): boolean {
    return toolState === ToolState.Live;
  }

}
