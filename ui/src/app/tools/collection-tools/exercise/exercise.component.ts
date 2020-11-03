import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  ExerciseGQL,
  ExerciseQuery,
  ExerciseSolveFieldsFragment,
  FlaskExerciseContentFragment,
  ProgrammingExerciseContentFragment,
  RegexExerciseContentFragment,
  SqlExerciseContentFragment,
  UmlExerciseContentFragment,
  WebExerciseContentFragment,
  XmlExerciseContentFragment
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

  get flaskExerciseContent(): FlaskExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'FlaskExerciseContent' ? this.exercise.content : undefined;
  }

  get programmingExerciseContent(): ProgrammingExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'ProgrammingExerciseContent' ? this.exercise.content : undefined;
  }

  get regexExerciseContent(): RegexExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'RegexExerciseContent' ? this.exercise.content : undefined;
  }

  get sqlExerciseContent(): SqlExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'SqlExerciseContent' ? this.exercise.content : undefined;
  }

  get umlExerciseContent(): UmlExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'UmlExerciseContent' ? this.exercise.content : undefined;
  }

  get webExerciseContent(): WebExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'WebExerciseContent' ? this.exercise.content : undefined;
  }

  get xmlExerciseContent(): XmlExerciseContentFragment | undefined {
    return this.exercise.content.__typename === 'XmlExerciseContent' ? this.exercise.content : undefined;
  }

}
