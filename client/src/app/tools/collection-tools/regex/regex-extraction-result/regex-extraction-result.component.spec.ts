import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegexExtractionResultComponent } from './regex-extraction-result.component';

describe('RegexExtractionResultComponent', () => {
  let component: RegexExtractionResultComponent;
  let fixture: ComponentFixture<RegexExtractionResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegexExtractionResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExtractionResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
