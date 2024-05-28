import { IPostOffice, NewPostOffice } from './post-office.model';

export const sampleWithRequiredData: IPostOffice = {
  id: 1898,
  name: 'uh-huh',
  streetAddress: 'grief beneath',
  city: 'Port Reinholdfield',
  state: 'instead interconnection',
  postalCode: '13338-4076',
};

export const sampleWithPartialData: IPostOffice = {
  id: 12713,
  name: 'lodge for credit',
  streetAddress: 'curiously dot towards',
  city: 'Hutchinson',
  state: 'defog among respectful',
  postalCode: '77640',
};

export const sampleWithFullData: IPostOffice = {
  id: 25454,
  name: 'actually poor where',
  streetAddress: 'antiquity',
  city: 'Port Diamondview',
  state: 'rudely oh',
  postalCode: '32067-6427',
};

export const sampleWithNewData: NewPostOffice = {
  name: 'joint whoa',
  streetAddress: 'survey',
  city: 'Kingfield',
  state: 'beautiful show-stopper',
  postalCode: '89606',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
