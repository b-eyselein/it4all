import {Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';
import {AdminReadExercisesGQL, AdminUpsertExerciseGQL, CompleteExerciseFragment} from "../../_services/apollo_services";
import {Saveable} from "../../_interfaces/saveable";
import {Subscription} from "rxjs";
import {ExerciseInterface} from "../../_interfaces/graphql-types";

@Component({templateUrl: './admin-read-exercises.component.html'})
export class AdminReadExercisesComponent implements OnInit, OnDestroy {


  private sub: Subscription;
  private toolId: string;

  savableExercises: Saveable<ExerciseInterface>[];

  @ViewChildren(ReadObjectComponent) readExerciseComponents: QueryList<ReadObjectComponent<CompleteExerciseFragment>>;


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

      this.adminReadExercisesGQL
        .watch({toolId: this.toolId, collId})
        .valueChanges
        .subscribe(({data}) => {
          this.savableExercises = data.tool.collection.readExercises
            .map((res) => {
              const exercise: ExerciseInterface = JSON.parse(res);
              return {saved: false, stringified: res, value: exercise, title: `${exercise.id}. ${exercise.title}`}
            });

          console.info(this.savableExercises.length);
        });
    })
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  save(exercise: Saveable<ExerciseInterface>): void {
    this.adminUpsertExerciseGQL
      .mutate({
        toolId: this.toolId,
        content: exercise.stringified
      })
      .subscribe(({data}) => exercise.saved = data.upsertExercise)
  }

  saveAll(): void {
    this.readExerciseComponents.forEach((readExerciseComponent) => readExerciseComponent.save.emit());
  }

}
