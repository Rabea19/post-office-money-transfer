import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CitizenDetailComponent } from './citizen-detail.component';

describe('Citizen Management Detail Component', () => {
  let comp: CitizenDetailComponent;
  let fixture: ComponentFixture<CitizenDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CitizenDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CitizenDetailComponent,
              resolve: { citizen: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CitizenDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CitizenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load citizen on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CitizenDetailComponent);

      // THEN
      expect(instance.citizen()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
