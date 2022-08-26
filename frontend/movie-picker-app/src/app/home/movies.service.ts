import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Movie} from "./movie.model";
import {environment} from "../../environments/environment";

interface MovieResponse {
  page: number;
  pageSize: number;
  elements: number;
  movies: Movie[]
}

@Injectable({
  providedIn: 'root'
})
export class MoviesService {

  constructor(private httpClient: HttpClient) {
  }

  fetchMovies(genres, year?, titleRegex?): Promise<MovieResponse> {
    let params = `?genres=${genres}`;

    if (year) {
      params += `&from=${year}&to=${year}`
    }
    if (titleRegex) {
      params += `&title=${titleRegex}`
    }
    console.log('genres picked: ', genres);
    try {
      return this.httpClient.get<MovieResponse>(environment.restServer + '/movies' + params).toPromise();
    } catch (e) {

    }
  }
}
