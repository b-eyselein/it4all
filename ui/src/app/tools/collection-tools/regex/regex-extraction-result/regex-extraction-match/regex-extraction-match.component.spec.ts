import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RegexExtractionMatchComponent} from './regex-extraction-match.component';
import {MatchType} from '../../../../../_interfaces/graphql-types';

describe('RegexExtractionMatchComponent', () => {
  let component: RegexExtractionMatchComponent;
  let fixture: ComponentFixture<RegexExtractionMatchComponent>;

  beforeEach(async(() => {
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
