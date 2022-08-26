export class Movie {
  constructor(
    public id: string,
    public title: string,
    public year: number,
    public genres: string[],
    public imgUrl: string
  ) {
  }
}

export enum Genre {
  ACTION,
  ADVENTURE,
  ANIMATION,
  CHILDREN,
  COMEDY,
  CRIME,
  DOCUMENTARY,
  DRAMA,
  FANTASY,
  FILM_NOIR,
  HORROR,
  IMAX,
  MUSICAL,
  MYSTERY,
  ROMANCE,
  SCI_FI,
  THRILLER,
  WAR,
  WESTERN,
}

export const genresList = [
  'ACTION',
  'ADVENTURE',
  'ANIMATION',
  'CHILDREN',
  'COMEDY',
  'CRIME',
  'DOCUMENTARY',
  'DRAMA',
  'FANTASY',
  'FILM_NOIR',
  'HORROR',
  'IMAX',
  'MUSICAL',
  'MYSTERY',
  'ROMANCE',
  'SCI_FI',
  'THRILLER',
  'WAR',
  'WESTERN'];
