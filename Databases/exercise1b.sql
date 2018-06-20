select title, G3.genre FROM movies
JOIN genres AS G1 ON movies.id = G1.movie_id
JOIN genres AS G2 ON G1.movie_id = G2.movie_id 
JOIN genres AS G3 ON G2.movie_id = G3.movie_id 
where G1.genre = 'Romance' and G2.genre = 'Comedy' and G3.genre<>G1.genre and G3.genre<>G2.genre

order by title



