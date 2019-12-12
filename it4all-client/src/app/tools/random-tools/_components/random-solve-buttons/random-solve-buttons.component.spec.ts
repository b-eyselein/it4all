import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RandomSolveButtonsComponent } from './random-solve-buttons.component';

describe('SolveButtonsComponent', () => {
  let component: RandomSolveButtonsComponent;
  let fixture: ComponentFixture<RandomSolveButtonsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RandomSolveButtonsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RandomSolveButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
