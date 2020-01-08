import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadObjectComponent } from './read-object.component';

describe('ReadObjectComponent', () => {
  let component: ReadObjectComponent;
  let fixture: ComponentFixture<ReadObjectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReadObjectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
