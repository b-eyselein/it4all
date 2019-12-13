import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminReadExercisesComponent} from './admin-read-exercises.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('AdminReadExercisesComponent', () => {
  let component: AdminReadExercisesComponent;
  let fixture: ComponentFixture<AdminReadExercisesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule],
      declarations: [AdminReadExercisesComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadExercisesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
