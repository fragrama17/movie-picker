import {Component} from '@angular/core';
import {MoviesService} from "./movies.service";
import {genresList, Movie} from "./movie.model";


@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  isLoading = false;
  moviePicked: Movie;
  genres: string[];
  titleTyped: string;
  genresPicked: string[];
  yearPicked: number;
  isYearFilterEnabled: boolean;

  constructor(private readonly moviesService: MoviesService) {
    this.genres = genresList;
    this.genresPicked = [];
    this.isYearFilterEnabled = true;
  }

  onPickerClicked() {
    console.log('fetching a movie for you tonight..');
    this.isLoading = true;
    this.moviesService.fetchMovies(this.genresPicked, this.yearPicked, this.titleTyped)
      .then(res => {
        this.isLoading = false;
        console.log('actual movies size', res.movies.length)
        let randIndex = Math.round(Math.random() * (res.movies.length - 1));
        console.log('random index: ', randIndex);
        this.moviePicked = res.movies[randIndex]
      });
  }

  onClearClicked() {
    this.moviePicked = null;
  }

  onTitleTyped($event) {
    this.titleTyped = $event.target.value;
  }

  onGenresPicked($event: any) {
    this.genresPicked = $event.target.value;
  }

  onYearPicked($event: any) {
    this.yearPicked = $event.target.value.split('-')[0];
    console.log('changed year:', this.yearPicked);
  }

  onYearEnabler() {
    this.yearPicked = null;
    this.isYearFilterEnabled = !this.isYearFilterEnabled;
  }

}
