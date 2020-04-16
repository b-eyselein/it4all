import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
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

@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit {

  tool: CollectionTool;
  collectionId: number;
  exerciseId: number;

  exerciseQuery: ExerciseQuery;

  oldPart: ToolPart;

  constructor(private route: ActivatedRoute, private exerciseGQL: ExerciseGQL) {
    this.route.paramMap.subscribe((paramMap) => {
      this.tool = collectionTools.find((t) => t.id === paramMap.get('toolId'));
      this.oldPart = this.tool.parts.find((p) => p.id === paramMap.get('partId'));

      this.collectionId = parseInt(paramMap.get('collId'), 10);
      this.exerciseId = parseInt(paramMap.get('exId'), 10);
    });
  }

  ngOnInit() {
    this.exerciseGQL
      .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
      .valueChanges
      .subscribe(({data}) => this.exerciseQuery = data);
  }

  private get exContent() {
    return this.exerciseQuery.tool.collection.exercise;
  }

  get progExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === "ProgrammingExercise") ? this.exContent : undefined;
  }

  get regexExerciseContent(): RegexExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'RegexExercise') ? this.exContent : undefined;
  }

  get sqlExerciseContent(): SqlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'SqlExercise') ? this.exContent : undefined;
  }

  get umlExerciseContent(): UmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'UmlExercise') ? this.exContent : undefined;
  }

  get webExerciseContent(): WebExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === "WebExercise") ? this.exContent : undefined;
  }

  get xmlExerciseContent(): XmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'XmlExercise') ? this.exContent : undefined;
  }

}
