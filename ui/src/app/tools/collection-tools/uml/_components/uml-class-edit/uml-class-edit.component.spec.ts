import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlClassEditComponent } from './uml-class-edit.component';

describe('UmlClassEditComponent', () => {
  let component: UmlClassEditComponent;
  let fixture: ComponentFixture<UmlClassEditComponent>;

  beforeEach(async(() => {
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
