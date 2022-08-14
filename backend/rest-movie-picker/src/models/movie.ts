import mongoose, {Schema} from 'mongoose';

const movieSchema = new Schema({
    id: {
        type: String,
        required: true
    },
    title: {
        type: String,
        required: true
    },
    year: {
        type: Number,
        required: false
    },
    genres: {
        type: Array,
        required: false
    }

});

export const Movie = mongoose.model('Movie', movieSchema);
