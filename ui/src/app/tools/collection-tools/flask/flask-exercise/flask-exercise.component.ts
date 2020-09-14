import {Component, Input, OnInit} from '@angular/core';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  FlaskExerciseContentFragment
} from "../../../../_services/apollo_services";
import {ComponentWithExerciseDirective} from "../../_helpers/component-with-exercise.directive";
import {FilesSolution, FilesSolutionInput, FlaskExercisePart} from "../../../../_interfaces/graphql-types";
import {AuthenticationService} from "../../../../_services/authentication.service";
import {DexieService} from "../../../../_services/dexie.service";
import {
  FlaskCorrectionGQL,
  FlaskCorrectionMutation,
  FlaskCorrectionMutationVariables
} from "../flask-apollo-mutations.service";

import 'codemirror/mode/htmlmixed/htmlmixed';
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
  extends ComponentWithExerciseDirective<FilesSolution,
    FilesSolutionInput,
    FlaskCorrectionMutation,
    FlaskExercisePart,
    FlaskCorrectionMutationVariables,
    FlaskCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: FlaskExerciseContentFragment;

  partId: string = getIdForFlaskExPart(FlaskExercisePart.FlaskSingleExPart);

  exerciseFileFragments: ExerciseFileFragment[] = [];

  constructor(private authenticationService: AuthenticationService, flaskCorrectionGQL: FlaskCorrectionGQL, dexieService: DexieService) {
    super(flaskCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.loadOldSolutionAbstract(
      this.exerciseFragment,
      this.partId,
      (oldSol) => this.exerciseFileFragments = oldSol.files
    );
  }

  protected getMutationQueryVariables(part: FlaskExercisePart): FlaskCorrectionMutationVariables {
    return undefined;
  }

  protected getSolution(): FilesSolutionInput | undefined {
    return undefined;
  }

  protected get sampleSolutions(): FilesSolution[] {
    return [];
  }

}
