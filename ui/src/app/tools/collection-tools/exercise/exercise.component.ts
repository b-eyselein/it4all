import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  ExerciseGQL,
  ExerciseQuery, ExerciseSolveFieldsFragment,
  ProgExerciseContentSolveFieldsFragment,
  RegexExerciseContentSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment,
  UmlExerciseContentSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment,
  XmlExerciseContentSolveFieldsFragment
} from '../../../_services/apollo_services';
import {Subscription} from 'rxjs';
import {AuthenticationService} from '../../../_services/authentication.service';


@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private apolloSub: Subscription;

  exerciseQuery: ExerciseQuery;

  constructor(
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private exerciseGQL: ExerciseGQL
  ) {
  }

  ngOnInit() {
    const userJwt = this.authenticationService.currentUserValue.jwt;

    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);
      const partId = paramMap.get('partId');

      this.apolloSub = this.exerciseGQL
        .watch({userJwt, toolId, collId, exId, partId})
        .valueChanges
        .subscribe(({data}) => this.exerciseQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.apolloSub.unsubscribe();
    this.sub.unsubscribe();
  }

  // Exercise content

  get exercise(): ExerciseSolveFieldsFragment {
    return this.exerciseQuery.me.tool.collection.exercise;
  }

  get programmingExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.programmingContent;
  }

  get regexExerciseContent(): RegexExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.regexContent;
  }

  get sqlExerciseContent(): SqlExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.sqlContent;
  }

  get umlExerciseContent(): UmlExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.umlContent;
  }

  get webExerciseContent(): WebExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.webContent;
  }

  get xmlExerciseContent(): XmlExerciseContentSolveFieldsFragment | undefined {
    return this.exercise.xmlContent;
  }

}
