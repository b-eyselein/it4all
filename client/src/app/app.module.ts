import {BrowserModule, Title} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppRoutingModule, routingComponents} from './app-routing.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {JwtInterceptor} from './_helpers/jwt.interceptor';
import {ErrorInterceptor} from './_helpers/error.interceptor';
import {NaryNumberReadOnlyInputComponent} from './tools/random-tools/nary/_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {SolutionSavedComponent} from './tools/collection-tools/tool-helpers/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from './tools/collection-tools/tool-helpers/points-notification/points-notification.component';
import {RegexMatchingResultComponent} from './tools/collection-tools/regex/regex-matching-result/regex-matching-result.component';
import {RegexExtractionResultComponent} from './tools/collection-tools/regex/regex-extraction-result/regex-extraction-result.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {ExerciseFilesEditorComponent} from './tools/collection-tools/tool-helpers/exercise-files-editor/exercise-files-editor.component';
import {TabComponent} from './_component_helpers/tab/tab.component';
import {TabsComponent} from './_component_helpers/tabs/tabs.component';
import {BoolCreateInstructionsComponent} from './tools/random-tools/bool/bool-create/bool-create-instructions/bool-create-instructions.component';
import {ExerciseFileCardComponent} from './tools/collection-tools/tool-helpers/exercise-file-card/exercise-file-card.component';
import {RandomSolveButtonsComponent} from './tools/random-tools/_components/random-solve-buttons/random-solve-buttons.component';
import {RegexExtractionMatchComponent} from './tools/collection-tools/regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {RegexCheatsheetComponent} from './tools/collection-tools/regex/regex-cheatsheet/regex-cheatsheet.component';
import {UmlTestComponent} from './tools/collection-tools/uml/uml-test/uml-test.component';
import {ReadCollectionComponent} from './admin/admin-read-collections/read-collection/read-collection.component';
import {ReadExerciseComponent} from './admin/admin-read-exercises/read-exercise/read-exercise.component';


@NgModule({
  declarations: [
    AppComponent,
    routingComponents,
    NaryNumberReadOnlyInputComponent,
    SolutionSavedComponent,
    PointsNotificationComponent,
    RegexMatchingResultComponent,
    RegexExtractionResultComponent,
    ExerciseFilesEditorComponent,
    TabComponent,
    TabsComponent,
    BoolCreateInstructionsComponent,
    ReadCollectionComponent,
    ReadExerciseComponent,
    ExerciseFileCardComponent,
    RandomSolveButtonsComponent,
    RegexExtractionMatchComponent,
    RegexCheatsheetComponent,
    UmlTestComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CodemirrorModule
  ],
  providers: [
    Title,
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
