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
import {BoolDrawingComponent} from './tools/random-tools/bool/bool-drawing/bool-drawing.component';
import {ProgrammingSimplifiedResultComponent} from './tools/collection-tools/programming/_results/programming-simplified-result/programming-simplified-result.component';
import {ProgrammingUnittestResultComponent} from './tools/collection-tools/programming/_results/programming-unittest-result/programming-unittest-result.component';
import {SqlResultComponent} from './tools/collection-tools/sql/sql-result/sql-result.component';
import {SqlMatchingResultComponent} from './tools/collection-tools/sql/sql-matching-result/sql-matching-result.component';
import { LessonTextContentComponent } from './tutorials/lesson-text-content/lesson-text-content.component';
import { TagComponent } from './tools/_components/tag/tag.component';


@NgModule({
  declarations: [
    AppComponent,
    routingComponents,

    ExerciseFilesEditorComponent, ExerciseFileCardComponent,

    SolutionSavedComponent, PointsNotificationComponent,

    // Tabs
    TabComponent, TabsComponent,

    RandomSolveButtonsComponent,

    // Nary
    NaryNumberReadOnlyInputComponent,
    // Bool
    BoolCreateInstructionsComponent, BoolDrawingComponent,
    // Regex
    RegexMatchingResultComponent, RegexExtractionResultComponent, RegexExtractionMatchComponent, RegexCheatsheetComponent,
    // Uml
    UmlTestComponent,
    // Programming
    ProgrammingSimplifiedResultComponent, ProgrammingUnittestResultComponent,
    // Sql
    SqlResultComponent, SqlMatchingResultComponent,

    // Admin
    ReadCollectionComponent, ReadExerciseComponent, LessonTextContentComponent, TagComponent
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
