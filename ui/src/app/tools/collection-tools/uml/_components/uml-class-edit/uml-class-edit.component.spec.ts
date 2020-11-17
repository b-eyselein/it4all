import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlClassEditComponent } from './uml-class-edit.component';

describe('UmlClassEditComponent', () => {
  let component: UmlClassEditComponent;
  let fixture: ComponentFixture<UmlClassEditComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlClassEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlClassEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
