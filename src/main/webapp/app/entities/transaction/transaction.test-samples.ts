import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 13997,
  amount: 3476.6,
  transactionDate: dayjs('2024-05-24'),
  status: 'PENDING',
};

export const sampleWithPartialData: ITransaction = {
  id: 17096,
  amount: 22586.27,
  transactionDate: dayjs('2024-05-24'),
  status: 'CANCELLED',
};

export const sampleWithFullData: ITransaction = {
  id: 13950,
  amount: 10984.9,
  transactionDate: dayjs('2024-05-24'),
  status: 'COMPLETED',
};

export const sampleWithNewData: NewTransaction = {
  amount: 21339.24,
  transactionDate: dayjs('2024-05-24'),
  status: 'CANCELLED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
