import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegexMatchingResultComponent } from './regex-matching-result.component';

describe('RegexMatchingResultComponent', () => {
  let component: RegexMatchingResultComponent;
  let fixture: ComponentFixture<RegexMatchingResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegexMatchingResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexMatchingResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
