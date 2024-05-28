import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 6658,
  login: 'r-sDG',
};

export const sampleWithPartialData: IUser = {
  id: 4059,
  login: 'SP_m',
};

export const sampleWithFullData: IUser = {
  id: 10114,
  login: 'X~S*@yJuz\\=G\\uCK1\\6x',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
