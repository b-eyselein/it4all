import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {
  ExerciseGQL,
  ExerciseQuery,
  ProgExerciseContentSolveFieldsFragment,
  RegexExerciseContentSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment,
  UmlExerciseContentSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment,
  XmlExerciseContentSolveFieldsFragment
} from "../../../_services/apollo_services";
import {Subscription} from "rxjs";


@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit, OnDestroy {

  private sub: Subscription;
  exerciseQuery: ExerciseQuery;

  constructor(private route: ActivatedRoute, private exerciseGQL: ExerciseGQL) {
  }

  ngOnInit() {
    this.sub = this.route.paramMap.subscribe((paramMap) => {
      const toolId = paramMap.get('toolId');
      const collId = parseInt(paramMap.get('collId'), 10);
      const exId = parseInt(paramMap.get('exId'), 10);
      const partId = paramMap.get('partId');

      this.exerciseGQL
        .watch({toolId, collId, exId, partId})
        .valueChanges
        .subscribe(({data}) => this.exerciseQuery = data);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  // Exercise content

  private get exercise() {
    return this.exerciseQuery.tool.collection.exercise;
  }

  get progExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
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
