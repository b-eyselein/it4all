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


@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  private apolloSub: Subscription;

  exerciseQuery: ExerciseQuery;

  constructor(private route: ActivatedRoute, private exerciseGQL: ExerciseGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);
      const partId = paramMap.get('partId');

      this.apolloSub = this.exerciseGQL
        .watch({toolId, collId, exId, partId})
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

  get flaskExerciseContent(): FlaskExerciseContentFragment | null {
    return this.exercise.content.__typename === 'FlaskExerciseContent' ? this.exercise.content : null;
  }

  get programmingExerciseContent(): ProgrammingExerciseContentFragment | null {
    return this.exercise.content.__typename === 'ProgrammingExerciseContent' ? this.exercise.content : null;
  }

  get regexExerciseContent(): RegexExerciseContentFragment | null {
    return this.exercise.content.__typename === 'RegexExerciseContent' ? this.exercise.content : null;
  }

  get sqlExerciseContent(): SqlExerciseContentFragment | null {
    return this.exercise.content.__typename === 'SqlExerciseContent' ? this.exercise.content : null;
  }

  get umlExerciseContent(): UmlExerciseContentFragment | null {
    return this.exercise.content.__typename === 'UmlExerciseContent' ? this.exercise.content : null;
  }

  get webExerciseContent(): WebExerciseContentFragment | null {
    return this.exercise.content.__typename === 'WebExerciseContent' ? this.exercise.content : null;
  }

  get xmlExerciseContent(): XmlExerciseContentFragment | null {
    return this.exercise.content.__typename === 'XmlExerciseContent' ? this.exercise.content : null;
  }

}
