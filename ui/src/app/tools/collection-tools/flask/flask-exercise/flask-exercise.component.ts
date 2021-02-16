import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  FilesSolution,
  FilesSolutionInput,
  FlaskAbstractCorrectionResultFragment,
  FlaskCorrectionGQL,
  FlaskCorrectionMutation,
  FlaskCorrectionMutationVariables,
  FlaskCorrectionResultFragment,
  FlaskExerciseContentFragment,
  FlaskExercisePart,
  FlaskResultFragment
} from "../../../../_services/apollo_services";
import {ComponentWithExerciseDirective} from "../../_helpers/component-with-exercise.directive";
import {DexieService} from "../../../../_services/dexie.service";
import {FilesExerciseComponent} from "../../_components/files-exercise/files-exercise.component";

import 'codemirror/mode/jinja2/jinja2';
import 'codemirror/mode/python/python';


function getIdForFlaskExPart(flaskExPart: FlaskExercisePart): string {
  switch (flaskExPart) {
    case FlaskExercisePart.FlaskSingleExPart:
      return 'solve';
  }
}

@Component({
  selector: 'it4all-flask-exercise',
  templateUrl: './flask-exercise.component.html'
})
export class FlaskExerciseComponent
  extends ComponentWithExerciseDirective<FilesSolutionInput, FlaskCorrectionMutation, FlaskCorrectionMutationVariables>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: FlaskExerciseContentFragment;

  @ViewChild(FilesExerciseComponent) filesExerciseComponent: FilesExerciseComponent;

  readonly partId: string = getIdForFlaskExPart(FlaskExercisePart.FlaskSingleExPart);

  exerciseFileFragments: ExerciseFileFragment[] = [];

  constructor(flaskCorrectionGQL: FlaskCorrectionGQL, dexieService: DexieService) {
    super(flaskCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.exerciseFileFragments = this.contentFragment.files;

    this.loadOldSolutionAbstract(
      this.exerciseFragment,
      this.partId,
      (oldSol) => this.exerciseFileFragments = oldSol.files
    );
  }

  // Sample solutions

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.flaskSampleSolutions;
  }

  // Correction

  protected getSolution(): FilesSolutionInput | undefined {
    return {files: this.exerciseFileFragments};
  }

  protected getMutationQueryVariables(): FlaskCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      part: FlaskExercisePart.FlaskSingleExPart,
      solution: this.getSolution()
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId, () => {
      if (this.filesExerciseComponent) {
        this.filesExerciseComponent.toggleCorrectionTab();
      }
    });
  }

  // Result

  get correctionResult(): FlaskCorrectionResultFragment | undefined {
    return this.resultQuery?.me.flaskExercise?.correct;
  }

  get abstractResult(): FlaskAbstractCorrectionResultFragment | undefined {
    return this.correctionResult?.result;
  }

  get result(): FlaskResultFragment | undefined {
    return (this.abstractResult.__typename === 'FlaskResult') ? this.abstractResult : undefined;
  }

}
