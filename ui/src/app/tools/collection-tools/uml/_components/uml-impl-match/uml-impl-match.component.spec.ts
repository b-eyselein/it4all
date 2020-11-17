import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlImplMatchComponent } from './uml-impl-match.component';

describe('UmlImplMatchComponent', () => {
  let component: UmlImplMatchComponent;
  let fixture: ComponentFixture<UmlImplMatchComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlImplMatchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlImplMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
