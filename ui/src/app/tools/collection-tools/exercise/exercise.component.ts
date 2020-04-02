import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../_services/api.service';
import {IExercise} from '../../../_interfaces/models';
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

  exercise: IExercise;

  oldPart: ToolPart;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private exerciseGQL: ExerciseGQL,
    private apiService: ApiService
  ) {
    this.route.paramMap.subscribe((paramMap) => {
      this.tool = collectionTools.find((t) => t.id === paramMap.get('toolId'));
      this.oldPart = this.tool.parts.find((p) => p.id === paramMap.get('partId'));

      this.collectionId = parseInt(paramMap.get('collId'), 10);
      this.exerciseId = parseInt(paramMap.get('exId'), 10);
    });
  }

  private updateExercise(): void {
    this.apiService.getExercise(this.tool.id, this.collectionId, this.exerciseId)
      .subscribe((ex: IExercise | undefined) => {
        if (ex) {
          this.exercise = ex;
        } else {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['../..']);
        }
      });
  }

  ngOnInit() {
    this.exerciseGQL
      .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
      .valueChanges
      .subscribe(({data}) => this.exerciseQuery = data);

    this.updateExercise();
  }

  private get exContent() {
    return this.exerciseQuery.tool.exerciseContent;
  }

  get progExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === "ProgExerciseContent") ? this.exContent : undefined;
  }

  get regexExerciseContent(): RegexExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'RegexExerciseContent') ? this.exContent : undefined;
  }

  get sqlExerciseContent(): SqlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'SqlExerciseContent') ? this.exContent : undefined;
  }

  get umlExerciseContent(): UmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'UmlExerciseContent') ? this.exContent : undefined;
  }

  get webExerciseContent(): WebExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === "WebExerciseContent") ? this.exContent : undefined;
  }

  get xmlExerciseContent(): XmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'XmlExerciseContent') ? this.exContent : undefined;
  }

}
