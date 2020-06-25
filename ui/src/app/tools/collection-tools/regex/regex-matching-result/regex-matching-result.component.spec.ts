import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RegexMatchingResultComponent} from './regex-matching-result.component';
import {FormsModule} from '@angular/forms';
import {BinaryClassificationResultType} from '../../../../_interfaces/graphql-types';

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
      resultType: BinaryClassificationResultType.TruePositive,
      matchData: 'md',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
