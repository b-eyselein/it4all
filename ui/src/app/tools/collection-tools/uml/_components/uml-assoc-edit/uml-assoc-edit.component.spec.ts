import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlAssocEditComponent } from './uml-assoc-edit.component';

describe('UmlAssocEditComponent', () => {
  let component: UmlAssocEditComponent;
  let fixture: ComponentFixture<UmlAssocEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlAssocEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlAssocEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
