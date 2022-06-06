import React, { useState, useEffect } from "react";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import Movies from "../backend/movies";
import {useUser} from "../hook/User";
import MovieTable from "../components/MovieTable";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  margin: 10px;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

const Search = () => {
    const {accessToken} = useUser();

    const [movieResults, setMovieResults] = useState([]);

    const {register, handleSubmit} = useForm({
        defaultValues: {
            title: null,
            year: null,
            director: null,
            genre: null,
            limit: 10,
            page: 1,
            orderBy: 'title',
            direction: 'asc'
        }
    });

    const submitMovieSearch = (data) => {
        const payLoad = {
            title: data.title,
            year: data.year,
            director: data.director,
            genre: data.genre,
            limit: data.limit,
            page: data.page,
            orderBy: data.orderBy,
            direction: data.direction
        }

        Movies.movieSearch(payLoad, accessToken)
            .then(response => (
                setMovieResults(response.data.movies)
                    // , alert(JSON.stringify(response.data, null, 2))
            ))
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <StyledDiv>
            <StyledDiv>
                <h1>Search</h1>
                <form onSubmit={handleSubmit(submitMovieSearch)}>
                    <input {...register("title")} placeholder={"Title"}/>
                    <input {...register("year")} placeholder={"Year"}/>
                    <input {...register("director")} placeholder={"Director"}/>
                    <input {...register("genre")} placeholder={"Genre"}/>
                    <select {...register("limit")}>
                        <option value={10}>10</option>
                        <option value={25}>25</option>
                        <option value={50}>50</option>
                        <option value={100}>100</option>
                    </select>
                    <input {...register("page")}/>
                    <select {...register("orderBy")}>
                        <option value="title">Title</option>
                        <option value="rating">Rating</option>
                        <option value="year">Year</option>
                    </select>
                    <select {...register("direction")}>
                        <option value="asc">Ascending</option>
                        <option value="desc">Descending</option>
                    </select>
                    <input type="submit"/>
                </form>
            </StyledDiv>

            <StyledDiv>
                <MovieTable movies={movieResults}></MovieTable>
            </StyledDiv>

        </StyledDiv>
    );
}

export default Search;
