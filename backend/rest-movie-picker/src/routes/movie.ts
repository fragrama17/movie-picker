import {Request, Response, Router} from 'express';
import {Movie} from '../models/movie';

interface DbQuery {
    year?: number;
    genres?: string[];
}

const router = Router();

router.get('/movies', async (req: Request, res: Response): Promise<void> => {
    let page = Number(req.query.page);
    let pageSize = Number(req.query.pageSize);
    const year = Number(req.query.year);
    const genresParam = req.query.genres as string;
    let genres: any[] = [];
    let dbQuery: DbQuery = {};

    if (!page || page < 0) page = 1;
    if (!pageSize || pageSize < 0) pageSize = 5;
    if (year) dbQuery.year = year;
    if (genresParam)
        genres = [...genresParam.trim().toUpperCase().split(',')];
    if (genres.length) dbQuery.genres = genres;

    //if page=1 nothing to skip -> page=0
    const movies = await Movie.find(dbQuery).skip((--page) * pageSize).limit(pageSize);

    res.send({
        page: ++page,
        pageSize: pageSize,
        elements: movies.length,
        movies: movies
    });
});


export {router};
