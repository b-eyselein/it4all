import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlExerciseComponent} from './sql-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';

describe('SqlExerciseComponent', () => {
  let component: SqlExerciseComponent;
  let fixture: ComponentFixture<SqlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule, CodemirrorModule],
      declarations: [SqlExerciseComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
