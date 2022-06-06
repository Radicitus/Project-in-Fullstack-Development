import React, {useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {useParams} from "react-router-dom";
import Movies from "../backend/movies";
import Billing from "../backend/billing";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

const MovieDetail = () => {
    const {accessToken} = useUser();
    const {movieId} = useParams();
    const [movieDetails, setMovieDetails] = useState([]);
    const [movieGenres, setMovieGenres] = useState([]);
    const [moviePeople, setMoviePeople] = useState([]);

    React.useEffect(() => {
        Movies.movieSearchByMovieId(movieId, accessToken)
            .then(response => (
                setMovieDetails(response.data.movie),
                    setMovieGenres(response.data.genres),
                    setMoviePeople(response.data.persons)
                // , alert(JSON.stringify(response.data, null, 2))
            ))
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }, [])

    const {register, getValues, handleSubmit} = useForm();

    const addToCart = () => {
        const quantity = getValues("quantity");

        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        Billing.insertIntoShoppingCart(payload, accessToken)
            .then(response =>
                alert("Item added to cart.")
            )
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <StyledDiv>
            {movieDetails &&
                <div>
                    <h1>{movieDetails.title}</h1>
                    <p>Year: {movieDetails.year}</p>
                    <p>Director: {movieDetails.director}</p>
                    <p>Rating: {movieDetails.rating}</p>
                    <p>Votes: {movieDetails.numVotes}</p>
                    <p>Budget: {movieDetails.budget}</p>
                    <p>Revenue: {movieDetails.revenue}</p>
                    <p>Overview: {movieDetails.overview}</p>
                    <img src={"https://image.tmdb.org/t/p/w300" + movieDetails.backdropPath} alt=""/>
                    <img src={"https://image.tmdb.org/t/p/w92" + movieDetails.posterPath} alt=""/>
                </div>
            }

            {movieGenres &&
                <div>
                    <h4>Genres:</h4>
                    <select>
                        {movieGenres.map(
                            genre => <option key={genre.id}>{genre.name}</option>
                        )}
                    </select>
                </div>
            }

            {moviePeople &&
                <div>
                    <h4>People:</h4>
                    <select>
                        {moviePeople.map(
                            person => <option key={person.id}>{person.name}</option>
                        )}
                    </select>
                </div>
            }

            <form>
                <input {...register("quantity")} type="number"/>
                <button onClick={handleSubmit(addToCart)}>Click to Add to Cart</button>
            </form>
        </StyledDiv>
    );
}

export default MovieDetail;
