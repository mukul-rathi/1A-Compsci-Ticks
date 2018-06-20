match (g:Genre)<-[:HAS_GENRE]- (m:Movie)<-[r]-(p:Person)
where type(r) = "WROTE"
and g.genre = "Drama"
return p.name as name, count(*) as total
order by total desc, name
limit 10;