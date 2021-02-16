import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  CollectionToolOverviewGQL,
  CollectionToolOverviewQuery,
  UserProficiencyFragment
} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {BreadCrumbPart} from "../../../shared/breadcrumbs/breadcrumbs.component";

@Component({templateUrl: './collection-tool-overview.component.html'})
export class CollectionToolOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;

  collectionToolOverviewQuery: CollectionToolOverviewQuery;

  constructor(private route: ActivatedRoute, private collectionToolOverviewGQL: CollectionToolOverviewGQL) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');

      this.collectionToolOverviewGQL
        .watch({toolId})
        .valueChanges
        .subscribe(({data}) => this.collectionToolOverviewQuery = data);
    });
  }

  get proficiencies(): UserProficiencyFragment[] {
    return this.collectionToolOverviewQuery.me.tool.proficiencies;
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get tool() {
    return this.collectionToolOverviewQuery.me.tool;
  }

  get breadCrumbs(): BreadCrumbPart[] {
    return [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name}
    ]
  }

}
