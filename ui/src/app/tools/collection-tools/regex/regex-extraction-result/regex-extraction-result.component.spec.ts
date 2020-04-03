import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RegexExtractionResultComponent} from './regex-extraction-result.component';
import {RegexExtractionMatchComponent} from './regex-extraction-match/regex-extraction-match.component';

describe('RegexExtractionResultComponent', () => {
  let component: RegexExtractionResultComponent;
  let fixture: ComponentFixture<RegexExtractionResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegexExtractionResultComponent, RegexExtractionMatchComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExtractionResultComponent);
    component = fixture.componentInstance;
    component.extractionResult = {
      extractionMatchingResult: {
        allMatches: [],
        points: -1,
        maxPoints: -1,
      },
      base: 'base',
      correct: true
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
