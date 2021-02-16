import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {RegexExtractionMatchComponent} from './regex-extraction-match.component';
import {MatchType} from '../../../../../_services/apollo_services';

describe('RegexExtractionMatchComponent', () => {
  let component: RegexExtractionMatchComponent;
  let fixture: ComponentFixture<RegexExtractionMatchComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RegexExtractionMatchComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExtractionMatchComponent);
    component = fixture.componentInstance;
    component.match = {
      userArg: null,
      sampleArg: '',
      matchType: MatchType.OnlySample
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
