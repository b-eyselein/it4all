import {Component, OnInit} from '@angular/core';
import {randomTools} from '../tools/random-tools/random-tools-list';
import {RandomTool} from '../_interfaces/tool';
import {CollectionToolFragment, ToolOverviewGQL, ToolOverviewQuery} from '../_services/apollo_services';
import {ToolState} from '../_interfaces/graphql-types';
import {AuthenticationService} from '../_services/authentication.service';

@Component({templateUrl: './tool-overview.component.html'})
export class ToolOverviewComponent implements OnInit {

  randomTools: RandomTool[] = randomTools;

  toolOverviewQuery: ToolOverviewQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private toolOverviewGQL: ToolOverviewGQL
  ) {
  }

  ngOnInit(): void {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.toolOverviewGQL
      .watch({userJwt})
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
