export interface Evento {
  id?: number;
  titulo: string;
  descricao: string;
  dataHora: string;
  local: string;
  deleted?: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
