import { ICitizen, NewCitizen } from './citizen.model';

export const sampleWithRequiredData: ICitizen = {
  id: 14832,
  nationalId: '24395639330018',
  firstName: 'Dereck',
  lastName: 'Dach',
  phoneNumber: '18797750790',
};

export const sampleWithPartialData: ICitizen = {
  id: 14863,
  nationalId: '77942119942865',
  firstName: 'Magnus',
  lastName: 'Kertzmann',
  phoneNumber: '77285640489',
};

export const sampleWithFullData: ICitizen = {
  id: 4188,
  nationalId: '63739384743174',
  firstName: 'Vance',
  lastName: 'Orn',
  phoneNumber: '89913209530',
};

export const sampleWithNewData: NewCitizen = {
  nationalId: '26395614698950',
  firstName: 'Allene',
  lastName: 'Rippin',
  phoneNumber: '50644237340',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
