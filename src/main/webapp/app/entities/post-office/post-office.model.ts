export interface IPostOffice {
  id: number;
  name?: string | null;
  streetAddress?: string | null;
  city?: string | null;
  state?: string | null;
  postalCode?: string | null;
}

export type NewPostOffice = Omit<IPostOffice, 'id'> & { id: null };
