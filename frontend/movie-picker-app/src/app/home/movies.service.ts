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

interface TrailerResponse {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class MoviesService {

  constructor(private httpClient: HttpClient) {
  }

  fetchMovies(genres: string[], from: number, to: number, titleRegex: string): Promise<MovieResponse> {
    let params = `?genres=${genres}`;

    if (from && to) {
      params += `&from=${from}&to=${to}`
    }
    if (titleRegex) {
      params += `&title=${titleRegex}`
    }

    return this.httpClient.get<MovieResponse>(environment.moviesServer + `/movies${params}`).toPromise();

  }

  fetchTrailer(title: string, year: number) {
    return this.httpClient.get<TrailerResponse>(environment.trailersServer + `/trailer?title=${title}+${year}+movie+trailer`).toPromise();

  }

}
