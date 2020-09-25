import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  ExerciseOverviewFragment,
  ExerciseOverviewGQL,
  ExerciseOverviewQuery,
  PartFragment
} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';
import {BreadCrumbPart} from "../../../shared/breadcrumbs/breadcrumbs.component";

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private exerciseOverviewQuery: ExerciseOverviewQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private exerciseOverviewGQL: ExerciseOverviewGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);

      this.exerciseOverviewGQL
        .watch({userJwt, toolId, collId, exId})
        .valueChanges
        .subscribe(({data}) => this.exerciseOverviewQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private getTool() {
    return this.exerciseOverviewQuery?.me.tool;
  }

  get exercise(): ExerciseOverviewFragment | undefined {
    return this.exerciseOverviewQuery?.me.tool.collection.exercise;
  }

  get entryParts(): PartFragment[] {
    return this.exercise.parts.filter((p) => p.isEntryPart);
  }

  get breadCrumbs(): BreadCrumbPart[] {
    return [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${this.getTool().id}`, title: this.getTool().name},
      {routerLinkPart: 'collections', title: 'Sammlungen'},
      {routerLinkPart: this.getTool().collection.collectionId.toString(), title: this.getTool().collection.title},
      {routerLinkPart: 'exercises', title: 'Aufgaben'},
      {routerLinkPart: this.exercise.exerciseId.toString(), title: this.exercise.exerciseId.toString()}
    ]
  }

}
