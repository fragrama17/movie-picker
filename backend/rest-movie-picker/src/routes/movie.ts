import {Request, Response, Router} from 'express';
import {Movie} from '../models/movie';

interface DbQuery {
    title?: {
        $regex: string;
    };
    year?: number | {
        $gte: number,
        $lte: number
    };
    genres?: {
        $all: string[]
    };
}

const router = Router();

router.get('/movies', async (req: Request, res: Response): Promise<void> => {
    let page = Number(req.query.page);
    let pageSize = Number(req.query.pageSize);
    const titleRegex = req.query.title as string;
    const from = Number(req.query.from);
    const to = Number(req.query.to);
    const genresParam = req.query.genres as string;
    let genres: any[] = [];
    let dbQuery: DbQuery = {};

    if (!page || page < 0) page = 1;
    if (!pageSize || pageSize < 0 || pageSize > 1000) pageSize = 1000;
    if (titleRegex) dbQuery.title = {$regex: titleRegex};
    if (from && to) dbQuery.year = {
        $gte: from,
        $lte: to
    };
    if (genresParam)
        genres = [...genresParam.trim().toUpperCase().split(',')];
    if (genres.length) dbQuery.genres = {$all: genres};

    //if page=1 nothing to skip -> page=0
    const movies = await Movie.find(dbQuery).skip((--page) * pageSize).limit(pageSize);

    console.log('sending the response to', req.ip,' at timestamp: ', new Date());
    res.send({
        page: ++page,
        pageSize: pageSize,
        elements: movies.length,
        movies: movies
    });
});


export {router};
