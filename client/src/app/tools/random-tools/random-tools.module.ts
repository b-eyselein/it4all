import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RandomSolveButtonsComponent} from './_components/random-solve-buttons/random-solve-buttons.component';
import {NaryNumberReadOnlyInputComponent} from './nary/_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {BoolCreateInstructionsComponent} from './bool/bool-create/bool-create-instructions/bool-create-instructions.component';
import {BoolDrawingComponent} from './bool/bool-drawing/bool-drawing.component';
import {randomToolRoutingComponents, RandomToolsRoutingModule} from './random-tools.routing';
import {FormsModule} from '@angular/forms';
import {SharedModule} from '../../shared/shared.module';


@NgModule({
  declarations: [
    RandomSolveButtonsComponent,
    NaryNumberReadOnlyInputComponent,
    BoolCreateInstructionsComponent, BoolDrawingComponent,

    ...randomToolRoutingComponents
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
