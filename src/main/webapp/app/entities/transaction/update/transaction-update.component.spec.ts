import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ICitizen } from 'app/entities/citizen/citizen.model';
import { CitizenService } from 'app/entities/citizen/service/citizen.service';
import { IPostOffice } from 'app/entities/post-office/post-office.model';
import { PostOfficeService } from 'app/entities/post-office/service/post-office.service';
import { ITransaction } from '../transaction.model';
import { TransactionService } from '../service/transaction.service';
import { TransactionFormService } from './transaction-form.service';

import { TransactionUpdateComponent } from './transaction-update.component';

describe('Transaction Management Update Component', () => {
  let comp: TransactionUpdateComponent;
  let fixture: ComponentFixture<TransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionFormService: TransactionFormService;
  let transactionService: TransactionService;
  let citizenService: CitizenService;
  let postOfficeService: PostOfficeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TransactionUpdateComponent],
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
      .overrideTemplate(TransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionFormService = TestBed.inject(TransactionFormService);
    transactionService = TestBed.inject(TransactionService);
    citizenService = TestBed.inject(CitizenService);
    postOfficeService = TestBed.inject(PostOfficeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Citizen query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const sender: ICitizen = { id: 21666 };
      transaction.sender = sender;
      const receiver: ICitizen = { id: 2884 };
      transaction.receiver = receiver;

      const citizenCollection: ICitizen[] = [{ id: 15483 }];
      jest.spyOn(citizenService, 'query').mockReturnValue(of(new HttpResponse({ body: citizenCollection })));
      const additionalCitizens = [sender, receiver];
      const expectedCollection: ICitizen[] = [...additionalCitizens, ...citizenCollection];
      jest.spyOn(citizenService, 'addCitizenToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(citizenService.query).toHaveBeenCalled();
      expect(citizenService.addCitizenToCollectionIfMissing).toHaveBeenCalledWith(
        citizenCollection,
        ...additionalCitizens.map(expect.objectContaining),
      );
      expect(comp.citizensSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PostOffice query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const senderPostOffice: IPostOffice = { id: 27625 };
      transaction.senderPostOffice = senderPostOffice;
      const receiverPostOffice: IPostOffice = { id: 28850 };
      transaction.receiverPostOffice = receiverPostOffice;

      const postOfficeCollection: IPostOffice[] = [{ id: 24883 }];
      jest.spyOn(postOfficeService, 'query').mockReturnValue(of(new HttpResponse({ body: postOfficeCollection })));
      const additionalPostOffices = [senderPostOffice, receiverPostOffice];
      const expectedCollection: IPostOffice[] = [...additionalPostOffices, ...postOfficeCollection];
      jest.spyOn(postOfficeService, 'addPostOfficeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(postOfficeService.query).toHaveBeenCalled();
      expect(postOfficeService.addPostOfficeToCollectionIfMissing).toHaveBeenCalledWith(
        postOfficeCollection,
        ...additionalPostOffices.map(expect.objectContaining),
      );
      expect(comp.postOfficesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transaction: ITransaction = { id: 456 };
      const sender: ICitizen = { id: 31101 };
      transaction.sender = sender;
      const receiver: ICitizen = { id: 31754 };
      transaction.receiver = receiver;
      const senderPostOffice: IPostOffice = { id: 22233 };
      transaction.senderPostOffice = senderPostOffice;
      const receiverPostOffice: IPostOffice = { id: 14072 };
      transaction.receiverPostOffice = receiverPostOffice;

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(comp.citizensSharedCollection).toContain(sender);
      expect(comp.citizensSharedCollection).toContain(receiver);
      expect(comp.postOfficesSharedCollection).toContain(senderPostOffice);
      expect(comp.postOfficesSharedCollection).toContain(receiverPostOffice);
      expect(comp.transaction).toEqual(transaction);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue(transaction);
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionService.update).toHaveBeenCalledWith(expect.objectContaining(transaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue({ id: null });
      jest.spyOn(transactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(transactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCitizen', () => {
      it('Should forward to citizenService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(citizenService, 'compareCitizen');
        comp.compareCitizen(entity, entity2);
        expect(citizenService.compareCitizen).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePostOffice', () => {
      it('Should forward to postOfficeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(postOfficeService, 'comparePostOffice');
        comp.comparePostOffice(entity, entity2);
        expect(postOfficeService.comparePostOffice).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
