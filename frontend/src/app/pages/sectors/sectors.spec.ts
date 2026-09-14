import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Sectors } from './sectors';

describe('Sectors', () => {
  let component: Sectors;
  let fixture: ComponentFixture<Sectors>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sectors],
    }).compileComponents();

    fixture = TestBed.createComponent(Sectors);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
