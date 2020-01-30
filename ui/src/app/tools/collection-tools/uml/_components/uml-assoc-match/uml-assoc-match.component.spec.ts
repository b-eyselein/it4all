import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlAssocMatchComponent } from './uml-assoc-match.component';

describe('UmlAssocMatchComponent', () => {
  let component: UmlAssocMatchComponent;
  let fixture: ComponentFixture<UmlAssocMatchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlAssocMatchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlAssocMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
