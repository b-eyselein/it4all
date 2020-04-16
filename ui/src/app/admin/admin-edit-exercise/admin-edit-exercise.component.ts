import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {AdminEditExerciseGQL, AdminEditExerciseQuery} from '../../_services/apollo_services';

@Component({
  selector: 'it4all-admin-edit-exercise',
  templateUrl: './admin-edit-exercise.component.html'
})
export class AdminEditExerciseComponent implements OnInit, OnDestroy {

  sub: Subscription;

  adminEditExerciseQuery: AdminEditExerciseQuery;

  constructor(private route: ActivatedRoute, private adminEditExerciseGQL: AdminEditExerciseGQL) {
  }

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);

      this.adminEditExerciseGQL
        .watch({toolId, collId, exId})
        .valueChanges
        .subscribe(({data}) => this.adminEditExerciseQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get exerciseAsJson(): object | undefined {
    return this.adminEditExerciseQuery.tool.collection.exercise;
  }

}
