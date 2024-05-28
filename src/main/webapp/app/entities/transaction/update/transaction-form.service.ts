import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITransaction, NewTransaction } from '../transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransaction for edit and NewTransactionFormGroupInput for create.
 */
type TransactionFormGroupInput = ITransaction | PartialWithRequiredKeyOf<NewTransaction>;

type TransactionFormDefaults = Pick<NewTransaction, 'id'>;

type TransactionFormGroupContent = {
  id: FormControl<ITransaction['id'] | NewTransaction['id']>;
  amount: FormControl<ITransaction['amount']>;
  transactionDate: FormControl<ITransaction['transactionDate']>;
  status: FormControl<ITransaction['status']>;
  sender: FormControl<ITransaction['sender']>;
  receiver: FormControl<ITransaction['receiver']>;
  senderPostOffice: FormControl<ITransaction['senderPostOffice']>;
  receiverPostOffice: FormControl<ITransaction['receiverPostOffice']>;
};

export type TransactionFormGroup = FormGroup<TransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransactionFormService {
  createTransactionFormGroup(transaction: TransactionFormGroupInput = { id: null }): TransactionFormGroup {
    const transactionRawValue = {
      ...this.getFormDefaults(),
      ...transaction,
    };
    return new FormGroup<TransactionFormGroupContent>({
      id: new FormControl(
        { value: transactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      amount: new FormControl(transactionRawValue.amount, {
        validators: [Validators.required],
      }),
      transactionDate: new FormControl(transactionRawValue.transactionDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(transactionRawValue.status, {
        validators: [Validators.required],
      }),
      sender: new FormControl(transactionRawValue.sender, {
        validators: [Validators.required],
      }),
      receiver: new FormControl(transactionRawValue.receiver, {
        validators: [Validators.required],
      }),
      senderPostOffice: new FormControl(transactionRawValue.senderPostOffice),
      receiverPostOffice: new FormControl(transactionRawValue.receiverPostOffice),
    });
  }

  getTransaction(form: TransactionFormGroup): ITransaction | NewTransaction {
    return form.getRawValue() as ITransaction | NewTransaction;
  }

  resetForm(form: TransactionFormGroup, transaction: TransactionFormGroupInput): void {
    const transactionRawValue = { ...this.getFormDefaults(), ...transaction };
    form.reset(
      {
        ...transactionRawValue,
        id: { value: transactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TransactionFormDefaults {
    return {
      id: null,
    };
  }
}
