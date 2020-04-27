import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AdminReadExercisesGQL, AdminUpsertExerciseGQL} from "../../_services/apollo_services";
import {Saveable} from "../../_interfaces/saveable";
import {Subscription} from "rxjs";
import {Exercise} from "../../_interfaces/graphql-types";

@Component({templateUrl: './admin-read-exercises.component.html'})
export class AdminReadExercisesComponent implements OnInit, OnDestroy {

  private toolId: string;

  private sub: Subscription;
  private apolloSub: Subscription;


  savableExercises: Saveable<Exercise>[];

  constructor(
    private route: ActivatedRoute,
    private adminReadExercisesGQL: AdminReadExercisesGQL,
    private adminUpsertExerciseGQL: AdminUpsertExerciseGQL,
  ) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      this.toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);

      this.apolloSub = this.adminReadExercisesGQL
        .watch({toolId: this.toolId, collId})
        .valueChanges
        .subscribe(({data}) => {
          this.savableExercises = data.tool.collection.readExercises
            .map((res) => {
              const exercise: Exercise = JSON.parse(res);

              return {
                saved: false,
                stringified: res,
                value: exercise,
                title: `${exercise.exerciseId}. ${exercise.title}`
              }
            });
        });
    })
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
    this.sub.unsubscribe();
  }

  save(exercise: Saveable<Exercise>): void {
    this.adminUpsertExerciseGQL
      .mutate({
        toolId: this.toolId,
        content: exercise.stringified
      })
      .subscribe(({data}) => exercise.saved = !!data.upsertExercise)
  }

  saveAll(): void {
    this.savableExercises
      .filter((s) => !s.saved)
      .forEach((ex) => this.save(ex));
  }

}
