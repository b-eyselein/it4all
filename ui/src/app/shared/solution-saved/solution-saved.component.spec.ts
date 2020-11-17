import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SolutionSavedComponent } from './solution-saved.component';

describe('SolutionSavedComponent', () => {
  let component: SolutionSavedComponent;
  let fixture: ComponentFixture<SolutionSavedComponent>;

  beforeEach(waitForAsync(() => {
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
