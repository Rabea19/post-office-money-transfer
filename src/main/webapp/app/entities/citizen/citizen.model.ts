export interface ICitizen {
  id: number;
  nationalId?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  phoneNumber?: string | null;
}

export type NewCitizen = Omit<ICitizen, 'id'> & { id: null };
