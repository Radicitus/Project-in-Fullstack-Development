import Config from "backend/config.json";
import Axios from "axios";


/**
 * We use axios to create REST calls to our backend
 *
 * We have provided the login rest call for your
 * reference to build other rest calls with.
 *
 * This is an async function. Which means calling this function requires that
 * you "chain" it with a .then() function call.
 * <br>
 * What this means is when the function is called it will essentially do it "in
 * another thread" and when the action is done being executed it will do
 * whatever the logic in your ".then()" function you chained to it
 * @example
 * login(request)
 * .then(response => alert(JSON.stringify(response.data, null, 2)));
 */

async function movieSearch(movieSearchRequest, accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.baseUrl,
        url: Config.movie.search,
        params: movieSearchRequest,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function movieSearchByMovieId(movieId, accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.baseUrl,
        url: Config.movie.movie_id + "/" + movieId,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    movieSearch, movieSearchByMovieId
}
