import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICitizen } from 'app/entities/citizen/citizen.model';
import { CitizenService } from 'app/entities/citizen/service/citizen.service';
import { IPostOffice } from 'app/entities/post-office/post-office.model';
import { PostOfficeService } from 'app/entities/post-office/service/post-office.service';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';
import { TransactionFormService, TransactionFormGroup } from './transaction-form.service';

@Component({
  standalone: true,
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  transaction: ITransaction | null = null;
  transactionStatusValues = Object.keys(TransactionStatus);

  citizensSharedCollection: ICitizen[] = [];
  postOfficesSharedCollection: IPostOffice[] = [];

  protected transactionService = inject(TransactionService);
  protected transactionFormService = inject(TransactionFormService);
  protected citizenService = inject(CitizenService);
  protected postOfficeService = inject(PostOfficeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TransactionFormGroup = this.transactionFormService.createTransactionFormGroup();

  compareCitizen = (o1: ICitizen | null, o2: ICitizen | null): boolean => this.citizenService.compareCitizen(o1, o2);

  comparePostOffice = (o1: IPostOffice | null, o2: IPostOffice | null): boolean => this.postOfficeService.comparePostOffice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.transaction = transaction;
      if (transaction) {
        this.updateForm(transaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.transactionFormService.getTransaction(this.editForm);
    if (transaction.id !== null) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(transaction: ITransaction): void {
    this.transaction = transaction;
    this.transactionFormService.resetForm(this.editForm, transaction);

    this.citizensSharedCollection = this.citizenService.addCitizenToCollectionIfMissing<ICitizen>(
      this.citizensSharedCollection,
      transaction.sender,
      transaction.receiver,
    );
    this.postOfficesSharedCollection = this.postOfficeService.addPostOfficeToCollectionIfMissing<IPostOffice>(
      this.postOfficesSharedCollection,
      transaction.senderPostOffice,
      transaction.receiverPostOffice,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.citizenService
      .query()
      .pipe(map((res: HttpResponse<ICitizen[]>) => res.body ?? []))
      .pipe(
        map((citizens: ICitizen[]) =>
          this.citizenService.addCitizenToCollectionIfMissing<ICitizen>(citizens, this.transaction?.sender, this.transaction?.receiver),
        ),
      )
      .subscribe((citizens: ICitizen[]) => (this.citizensSharedCollection = citizens));

    this.postOfficeService
      .query()
      .pipe(map((res: HttpResponse<IPostOffice[]>) => res.body ?? []))
      .pipe(
        map((postOffices: IPostOffice[]) =>
          this.postOfficeService.addPostOfficeToCollectionIfMissing<IPostOffice>(
            postOffices,
            this.transaction?.senderPostOffice,
            this.transaction?.receiverPostOffice,
          ),
        ),
      )
      .subscribe((postOffices: IPostOffice[]) => (this.postOfficesSharedCollection = postOffices));
  }
}
