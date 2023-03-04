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

export interface Result<T = any> {
  message: string;
  data: T;
  code: string;
}

export const emptyPageResult = () => {
  return {
    size: 20,
    number: 0,
    first: true,
    last: false,
    content: [],
  } as unknown as PageResult;
}
