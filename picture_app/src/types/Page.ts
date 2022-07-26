export interface PageResult<T = any> {
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
  last: boolean;
  totalPages: number;
  totalElements: number;
  content: T[];
}
