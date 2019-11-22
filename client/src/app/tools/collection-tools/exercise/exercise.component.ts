import {Component, OnInit} from '@angular/core';
import {ExerciseComponentHelpers} from '../_helpers/ExerciseComponentHelpers';
import {Exercise, ExerciseCollection, ExerciseContent} from '../../../_interfaces/exercise';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';

@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent extends ExerciseComponentHelpers<ExerciseContent> implements OnInit {

  collection: ExerciseCollection;
  exercise: Exercise<ExerciseContent>;

  constructor(private route: ActivatedRoute, private router: Router, private apiService: ApiService) {
    super(route);
  }

  private updateExercise(): void {
    const exerciseId = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise<ExerciseContent>(this.tool.id, this.collection.id, exerciseId)
      .subscribe((ex: Exercise<ExerciseContent> | undefined) => {
        if (ex) {
          this.exercise = ex;
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../..']);
        }
      });
  }

  ngOnInit() {
    const collectionId = parseInt(this.route.snapshot.paramMap.get('collId'), 10);

    this.apiService.getCollection(this.tool.id, collectionId)
      .subscribe((coll: ExerciseCollection | undefined) => {
        if (coll) {
          this.collection = coll;
          this.updateExercise();
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../../../..']);
        }
      });
  }

}
