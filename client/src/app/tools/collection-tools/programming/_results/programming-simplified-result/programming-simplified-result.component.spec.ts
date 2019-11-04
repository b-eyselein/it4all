import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammingSimplifiedResultComponent } from './programming-simplified-result.component';

describe('ProgrammingSimplifiedResultComponent', () => {
  let component: ProgrammingSimplifiedResultComponent;
  let fixture: ComponentFixture<ProgrammingSimplifiedResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingSimplifiedResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingSimplifiedResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
