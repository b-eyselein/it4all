import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {ExerciseComponentHelpers} from '../_helpers/ExerciseComponentHelpers';
import {Exercise, ExerciseContent} from '../../../_interfaces/exercise';

@Component({templateUrl: './exercise-overview.component.html'})
export class ExerciseOverviewComponent extends ExerciseComponentHelpers<ExerciseContent> implements OnInit {

  collectionId: number;
  exercise: Exercise<any>;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    super(route);

    if (!this.tool) {
      this.router.navigate(['../..']);
    }

    this.collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
  }

  ngOnInit() {
    const exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise<any>(this.tool.id, this.collectionId, exerciseId)
      .subscribe((exercise) => {
        console.info(JSON.stringify(exercise.content, null, 2));

        this.exercise = exercise;
      });
  }

}
