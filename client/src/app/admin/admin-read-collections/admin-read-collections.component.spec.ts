import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminReadCollectionsComponent } from './admin-read-collections.component';

describe('AdminReadCollectionsComponent', () => {
  let component: AdminReadCollectionsComponent;
  let fixture: ComponentFixture<AdminReadCollectionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminReadCollectionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadCollectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
