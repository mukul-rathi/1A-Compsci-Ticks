select title, count(*) as total
FROM movies
JOIN keywords ON movies.id = keywords.movie_id 
JOIN keywords AS skyfall_kw ON  (select id from movies where title like '%Skyfall%') = skyfall_kw.movie_id
where keywords.keyword = skyfall_kw.keyword and not title like '%Skyfall%'
group by title
 order by total desc, title
    limit 10;
