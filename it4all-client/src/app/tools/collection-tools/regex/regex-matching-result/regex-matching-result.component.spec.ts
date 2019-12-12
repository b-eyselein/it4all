import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RegexMatchingResultComponent} from './regex-matching-result.component';
import {FormsModule} from '@angular/forms';

describe('RegexMatchingResultComponent', () => {
  let component: RegexMatchingResultComponent;
  let fixture: ComponentFixture<RegexMatchingResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegexMatchingResultComponent],
      imports: [FormsModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexMatchingResultComponent);
    component = fixture.componentInstance;
    component.matchingResult = {
      resultType: 'TruePositive',
      matchData: 'md',
      isIncluded: true,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
