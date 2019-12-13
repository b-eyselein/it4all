import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RegexExerciseComponent} from './regex-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RegexCheatsheetComponent} from '../regex-cheatsheet/regex-cheatsheet.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {RegexMatchingResultComponent} from '../regex-matching-result/regex-matching-result.component';
import {RegexExtractionMatchComponent} from '../regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {RegexExtractionResultComponent} from '../regex-extraction-result/regex-extraction-result.component';
import {ApiService} from '../../_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {IRegexExerciseContent} from '../../../../_interfaces/models';

describe('RegexExerciseComponent', () => {

  let component: RegexExerciseComponent;
  let fixture: ComponentFixture<RegexExerciseComponent>;

  const exerciseContent: IRegexExerciseContent = {
    correctionType: 'EXTRACTION',
    extractionTestData: [],
    matchTestData: [],
    maxPoints: -1,
    sampleSolutions: []
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [
        RegexExerciseComponent,
        RegexCheatsheetComponent, SolutionSavedComponent,
        RegexMatchingResultComponent, RegexExtractionMatchComponent, RegexExtractionResultComponent],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExerciseComponent);
    component = fixture.componentInstance;
    component.exercise = {
      id: 0, collectionId: 0, toolId: 'regex', semanticVersion: {major: 0, minor: 1, patch: 0}, title: '',
      authors: [], text: '', tags: [], content: exerciseContent
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
