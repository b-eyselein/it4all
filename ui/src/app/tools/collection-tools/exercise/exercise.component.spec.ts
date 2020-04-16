import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ExerciseComponent} from './exercise.component';
import {ProgrammingExerciseComponent} from '../programming/programming-exercise/programming-exercise.component';
import {WebExerciseComponent} from '../web/web-exercise/web-exercise.component';
import {RegexExerciseComponent} from '../regex/regex-exercise/regex-exercise.component';
import {UmlExerciseComponent} from '../uml/uml-exercise/uml-exercise.component';
import {ExerciseFilesEditorComponent} from '../_components/exercise-files-editor/exercise-files-editor.component';
import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {TabComponent} from '../../../shared/tab/tab.component';
import {SolutionSavedComponent} from '../../../shared/solution-saved/solution-saved.component';
import {ProgrammingSimplifiedResultComponent} from '../programming/_results/programming-simplified-result/programming-simplified-result.component';
import {ExerciseFileCardComponent} from '../_components/exercise-file-card/exercise-file-card.component';
import {RouterTestingModule} from '@angular/router/testing';
import {PointsNotificationComponent} from '../../../shared/points-notification/points-notification.component';
import {RegexCheatsheetComponent} from '../regex/regex-cheatsheet/regex-cheatsheet.component';
import {RegexMatchingResultComponent} from '../regex/regex-matching-result/regex-matching-result.component';
import {RegexExtractionResultComponent} from '../regex/regex-extraction-result/regex-extraction-result.component';
import {RegexExtractionMatchComponent} from '../regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UmlClassSelectionComponent} from '../uml/uml-class-selection/uml-class-selection.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('ExerciseComponent', () => {
  let component: ExerciseComponent;
  let fixture: ComponentFixture<ExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        ExerciseComponent,
        ProgrammingExerciseComponent, WebExerciseComponent, RegexExerciseComponent, UmlExerciseComponent,
        ExerciseFilesEditorComponent, TabsComponent, TabComponent, SolutionSavedComponent, ProgrammingSimplifiedResultComponent,
        ExerciseFileCardComponent, SolutionSavedComponent, PointsNotificationComponent,
        RegexCheatsheetComponent, RegexMatchingResultComponent, RegexExtractionResultComponent, RegexExtractionMatchComponent,
        UmlClassSelectionComponent
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web'], ['collId', '0'], ['exId', '0'], ['partId', 'html']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
