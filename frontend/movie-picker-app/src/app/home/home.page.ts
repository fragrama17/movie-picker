import {Component} from '@angular/core';
import {MoviesService} from "./movies.service";
import {genresList, Movie} from "./movie.model";
import {AlertController} from "@ionic/angular";


@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  isLoading = false;
  isTrailerLoading = false;

  moviePicked: Movie;
  trailerUrlFetched: string;

  genres: string[];
  titleTyped: string;
  genresPicked: string[];
  fromYearPicked: number;
  toYearPicked: number;

  isYearFilterEnabled: boolean;

  constructor(private readonly moviesService: MoviesService, private alertController: AlertController) {
    this.genres = genresList;
    this.genresPicked = [];
    this.isYearFilterEnabled = false;
  }

  onPickerClicked() {
    if (this.fromYearPicked > this.toYearPicked) {
      this.alertController.create({
        message: 'Years range not allowed',
        buttons: ['Got It']
      }).then(alert => alert.present());
      return;
    }

    console.log('fetching a movie for you tonight..');
    this.isLoading = true;
    this.moviesService.fetchMovies(this.genresPicked, this.fromYearPicked, this.toYearPicked, this.titleTyped)
      .then(res => {
        this.isLoading = false;
        if (!res.movies.length) {
          this.moviePicked = HomePage.noMovieFound();
          return;
        }
        console.log('actual movies size', res.movies.length);
        let randIndex = Math.round(Math.random() * (res.movies.length - 1));
        console.log('random index: ', randIndex);
        this.moviePicked = res.movies[randIndex];
        this.isTrailerLoading = true;
        this.trailerUrlFetched = null;

        this.moviesService.fetchTrailer(this.moviePicked.title, this.moviePicked.year)
          .then(trailerUrl => {
            this.isTrailerLoading = false;
            this.trailerUrlFetched = trailerUrl.url;
          })
          .catch(() => {
            this.isTrailerLoading = false;
          });

      })
      .catch(() => {
        this.isLoading = false;
        this.alertController.create({
          message: 'Server not responding, retry later',
          buttons: ['Got It']
        }).then(alert => alert.present());
      });
  }

  private static noMovieFound() {
    return new Movie('', 'No movie found with this criteria', null, null, null);
  }

  onTitleTyped($event) {
    this.titleTyped = $event.target.value;
  }

  onGenresPicked($event: any) {
    this.genresPicked = $event.target.value;
  }

  onFromYearPicked($event: any) {
    this.fromYearPicked = $event.target.value.split('-')[0];
    console.log('changed From year:', this.fromYearPicked);
  }

  onToYearPicked($event: any) {
    this.toYearPicked = $event.target.value.split('-')[0];
    console.log('changed To year:', this.toYearPicked);
  }

  onYearEnabler() {
    this.fromYearPicked = null;
    this.toYearPicked = null;
    this.isYearFilterEnabled = !this.isYearFilterEnabled;
  }

}
