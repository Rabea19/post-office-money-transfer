import dayjs from 'dayjs/esm';
import { ICitizen } from 'app/entities/citizen/citizen.model';
import { IPostOffice } from 'app/entities/post-office/post-office.model';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

export interface ITransaction {
  id: number;
  amount?: number | null;
  transactionDate?: dayjs.Dayjs | null;
  status?: keyof typeof TransactionStatus | null;
  sender?: Pick<ICitizen, 'id' | 'nationalId'> | null;
  receiver?: Pick<ICitizen, 'id' | 'nationalId'> | null;
  senderPostOffice?: Pick<IPostOffice, 'id' | 'name'> | null;
  receiverPostOffice?: Pick<IPostOffice, 'id' | 'name'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
