import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilledPointsComponent } from './filled-points.component';

describe('FilledPointsComponent', () => {
  let component: FilledPointsComponent;
  let fixture: ComponentFixture<FilledPointsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilledPointsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilledPointsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
