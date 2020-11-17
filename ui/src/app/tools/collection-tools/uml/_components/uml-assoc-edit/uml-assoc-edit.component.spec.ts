import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlAssocEditComponent } from './uml-assoc-edit.component';

describe('UmlAssocEditComponent', () => {
  let component: UmlAssocEditComponent;
  let fixture: ComponentFixture<UmlAssocEditComponent>;

  beforeEach(waitForAsync(() => {
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
