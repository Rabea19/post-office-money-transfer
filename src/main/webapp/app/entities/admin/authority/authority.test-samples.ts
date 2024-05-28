import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'c43a95f5-8bfe-41b5-8339-275cb49d4b47',
};

export const sampleWithPartialData: IAuthority = {
  name: 'b211ff91-2407-437f-969a-0509c198781a',
};

export const sampleWithFullData: IAuthority = {
  name: 'b36bc87a-fe6c-4a69-b391-8fc3cfa93789',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
