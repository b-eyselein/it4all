import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {RegexMatchingResultComponent} from './regex-matching-result.component';
import {FormsModule} from '@angular/forms';
import {BinaryClassificationResultType} from '../../../../_services/apollo_services';

describe('RegexMatchingResultComponent', () => {
  let component: RegexMatchingResultComponent;
  let fixture: ComponentFixture<RegexMatchingResultComponent>;

  beforeEach(waitForAsync(() => {
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
