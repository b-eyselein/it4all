import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ExerciseOverviewGQL, ExerciseOverviewQuery, PartFragment} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  exerciseOverviewQuery: ExerciseOverviewQuery;

  constructor(private route: ActivatedRoute, private exerciseOverviewGQL: ExerciseOverviewGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);

      this.exerciseOverviewGQL
        .watch({toolId, collId, exId})
        .valueChanges
        .subscribe(({data}) => this.exerciseOverviewQuery = data);
    })
  }

  get parts(): PartFragment[] {
    return this.exerciseOverviewQuery.tool.collection.exercise.parts;
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

}
