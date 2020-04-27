import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdminEditExerciseGQL, AdminUpsertExerciseGQL} from '../../_services/apollo_services';

@Component({
  selector: 'it4all-admin-edit-exercise',
  templateUrl: './admin-edit-exercise.component.html'
})
export class AdminEditExerciseComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private apolloSub: Subscription;

  private toolId: string;

  exerciseToEdit: object;

  constructor(
    private route: ActivatedRoute,
    private adminEditExerciseGQL: AdminEditExerciseGQL,
    private adminUpsertExerciseGQL: AdminUpsertExerciseGQL
  ) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);

      this.apolloSub = this.adminEditExerciseGQL
        .watch({toolId, collId, exId})
        .valueChanges
        .subscribe(({data}) => this.exerciseToEdit = JSON.parse(data.tool.collection.exercise.asJsonString));
    });
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
    this.sub.unsubscribe();
  }

  save(): void {
    console.info(this.adminUpsertExerciseGQL.document);

    this.adminUpsertExerciseGQL
      .mutate({toolId: this.toolId, content: JSON.stringify(this.exerciseToEdit)})
      .subscribe(({data}) => {
        console.info(data);
      });
  }

}
