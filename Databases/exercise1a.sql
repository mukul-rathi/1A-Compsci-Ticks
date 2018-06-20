select title FROM movies
JOIN genres AS G1 ON movies.id = G1.movie_id
JOIN genres AS G2 ON G1.movie_id = G2.movie_id 
where G1.genre = 'Romance' and G2.genre = 'Comedy'
order by title

