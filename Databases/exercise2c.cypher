match (g1:Genre)<-[:HAS_GENRE]-(m:Movie)<-[:ACTS_IN]-(p:Person)-[:DIRECTED]->(m:Movie)-[:HAS_GENRE]->(g2:Genre)
where g1.genre = "Action"
and g2.genre = "Adventure"
return p.name as name, m.title as title
order by name, title;