import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PostOfficeDetailComponent } from './post-office-detail.component';

describe('PostOffice Management Detail Component', () => {
  let comp: PostOfficeDetailComponent;
  let fixture: ComponentFixture<PostOfficeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostOfficeDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PostOfficeDetailComponent,
              resolve: { postOffice: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PostOfficeDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostOfficeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load postOffice on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PostOfficeDetailComponent);

      // THEN
      expect(instance.postOffice()).toEqual(expect.objectContaining({ id: 123 }));
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
