import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RandomSolveButtonsComponent} from './_components/random-solve-buttons/random-solve-buttons.component';
import {NaryNumberReadOnlyInputComponent} from './nary/_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {BoolCreateInstructionsComponent} from './bool/bool-create/bool-create-instructions/bool-create-instructions.component';
import {BoolDrawingComponent} from './bool/bool-drawing/bool-drawing.component';
import {randomToolRoutingComponents, RandomToolsRoutingModule} from './random-tools.routing';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../../shared/shared.module';
import {BoolFillOutComponent} from "./bool/bool-fill-out/bool-fill-out.component";
import {BoolCreateComponent} from "./bool/bool-create/bool-create.component";
import {NaryAdditionComponent} from "./nary/nary-addition/nary-addition.component";
import {NaryConversionComponent} from "./nary/nary-conversion/nary-conversion.component";
import {NaryTwoConversionComponent} from "./nary/nary-two-conversion/nary-two-conversion.component";


@NgModule({
  declarations: [
    ...randomToolRoutingComponents,

    RandomSolveButtonsComponent,

    BoolFillOutComponent,
    BoolDrawingComponent,
    BoolCreateComponent,
    BoolCreateInstructionsComponent,

    NaryNumberReadOnlyInputComponent,
    NaryAdditionComponent,
    NaryConversionComponent,
    NaryTwoConversionComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    RandomToolsRoutingModule
  ]
})
export class RandomToolsModule {
}
