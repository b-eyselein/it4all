import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RegexCheatsheetComponent } from './regex-cheatsheet.component';

describe('RegexCheatsheetComponent', () => {
  let component: RegexCheatsheetComponent;
  let fixture: ComponentFixture<RegexCheatsheetComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RegexCheatsheetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexCheatsheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
