import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {ComponentWithCollectionTool} from '../_helpers/ComponentWithCollectionTool';
import {IExercise} from '../../../_interfaces/models';
import {ExerciseOverviewGQL, ExerciseOverviewQuery} from "../../../_services/apollo_services";

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent extends ComponentWithCollectionTool implements OnInit {

  collectionId: number;
  exerciseId: number;

  exercise: IExercise;

  exerciseOverviewQuery: ExerciseOverviewQuery;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private exerciseOverviewGQL: ExerciseOverviewGQL,
    private apiService: ApiService
  ) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['../..']);
    }

    this.collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    this.exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit() {
    this.exerciseOverviewGQL
      .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
      .valueChanges
      .subscribe(({data}) => this.exerciseOverviewQuery = data);

    this.apiService.getExercise(this.tool.id, this.collectionId, this.exerciseId)
      .subscribe((exercise) => this.exercise = exercise);
  }

}
