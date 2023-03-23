import express from 'express';
import {router as moviesRouter} from './routes/movie';
import bodyParser from 'body-parser';
import cors from 'cors';
import {connect} from 'mongoose';
import {dbUri} from "./config/dev";

const app = express();

app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
app.use(cors());
app.use('/api/v1', moviesRouter);

connect(dbUri)
    .then(() => app.listen(3000, () => {
        console.log('Connected to mongoDB');
        console.log('Listening on port 3000');
    }));
