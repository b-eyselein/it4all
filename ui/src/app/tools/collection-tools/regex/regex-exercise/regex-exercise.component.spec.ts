import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {RegexExerciseComponent} from './regex-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RegexCheatsheetComponent} from '../regex-cheatsheet/regex-cheatsheet.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {RegexMatchingResultComponent} from '../regex-matching-result/regex-matching-result.component';
import {RegexExtractionMatchComponent} from '../regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {RegexExtractionResultComponent} from '../regex-extraction-result/regex-extraction-result.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('RegexExerciseComponent', () => {

  let component: RegexExerciseComponent;
  let fixture: ComponentFixture<RegexExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [
        RegexExerciseComponent,
        RegexCheatsheetComponent, SolutionSavedComponent,
        RegexMatchingResultComponent, RegexExtractionMatchComponent, RegexExtractionResultComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
