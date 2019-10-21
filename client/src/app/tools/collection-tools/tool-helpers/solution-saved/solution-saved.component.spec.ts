import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SolutionSavedComponent } from './solution-saved.component';

describe('SolutionSavedComponent', () => {
  let component: SolutionSavedComponent;
  let fixture: ComponentFixture<SolutionSavedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SolutionSavedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolutionSavedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
