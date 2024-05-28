import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { CitizenService } from '../service/citizen.service';
import { ICitizen } from '../citizen.model';
import { CitizenFormService } from './citizen-form.service';

import { CitizenUpdateComponent } from './citizen-update.component';

describe('Citizen Management Update Component', () => {
  let comp: CitizenUpdateComponent;
  let fixture: ComponentFixture<CitizenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let citizenFormService: CitizenFormService;
  let citizenService: CitizenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CitizenUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CitizenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CitizenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    citizenFormService = TestBed.inject(CitizenFormService);
    citizenService = TestBed.inject(CitizenService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const citizen: ICitizen = { id: 456 };

      activatedRoute.data = of({ citizen });
      comp.ngOnInit();

      expect(comp.citizen).toEqual(citizen);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICitizen>>();
      const citizen = { id: 123 };
      jest.spyOn(citizenFormService, 'getCitizen').mockReturnValue(citizen);
      jest.spyOn(citizenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ citizen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: citizen }));
      saveSubject.complete();

      // THEN
      expect(citizenFormService.getCitizen).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(citizenService.update).toHaveBeenCalledWith(expect.objectContaining(citizen));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICitizen>>();
      const citizen = { id: 123 };
      jest.spyOn(citizenFormService, 'getCitizen').mockReturnValue({ id: null });
      jest.spyOn(citizenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ citizen: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: citizen }));
      saveSubject.complete();

      // THEN
      expect(citizenFormService.getCitizen).toHaveBeenCalled();
      expect(citizenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICitizen>>();
      const citizen = { id: 123 };
      jest.spyOn(citizenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ citizen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(citizenService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
