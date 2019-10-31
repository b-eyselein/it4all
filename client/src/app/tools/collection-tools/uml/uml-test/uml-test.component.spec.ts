import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlTestComponent } from './uml-test.component';

describe('UmlTestComponent', () => {
  let component: UmlTestComponent;
  let fixture: ComponentFixture<UmlTestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlTestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
