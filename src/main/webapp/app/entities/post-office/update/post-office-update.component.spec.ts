import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { PostOfficeService } from '../service/post-office.service';
import { IPostOffice } from '../post-office.model';
import { PostOfficeFormService } from './post-office-form.service';

import { PostOfficeUpdateComponent } from './post-office-update.component';

describe('PostOffice Management Update Component', () => {
  let comp: PostOfficeUpdateComponent;
  let fixture: ComponentFixture<PostOfficeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postOfficeFormService: PostOfficeFormService;
  let postOfficeService: PostOfficeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PostOfficeUpdateComponent],
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
      .overrideTemplate(PostOfficeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostOfficeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postOfficeFormService = TestBed.inject(PostOfficeFormService);
    postOfficeService = TestBed.inject(PostOfficeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const postOffice: IPostOffice = { id: 456 };

      activatedRoute.data = of({ postOffice });
      comp.ngOnInit();

      expect(comp.postOffice).toEqual(postOffice);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPostOffice>>();
      const postOffice = { id: 123 };
      jest.spyOn(postOfficeFormService, 'getPostOffice').mockReturnValue(postOffice);
      jest.spyOn(postOfficeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postOffice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postOffice }));
      saveSubject.complete();

      // THEN
      expect(postOfficeFormService.getPostOffice).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(postOfficeService.update).toHaveBeenCalledWith(expect.objectContaining(postOffice));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPostOffice>>();
      const postOffice = { id: 123 };
      jest.spyOn(postOfficeFormService, 'getPostOffice').mockReturnValue({ id: null });
      jest.spyOn(postOfficeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postOffice: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postOffice }));
      saveSubject.complete();

      // THEN
      expect(postOfficeFormService.getPostOffice).toHaveBeenCalled();
      expect(postOfficeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPostOffice>>();
      const postOffice = { id: 123 };
      jest.spyOn(postOfficeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postOffice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postOfficeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
