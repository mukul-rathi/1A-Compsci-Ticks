match (m:Movie)<-[:ACTS_IN]-(p:Person)-[:DIRECTED]->(m:Movie)<-[:PRODUCED]-(p:Person)-[:EDITED]->(m:Movie)
 return p.name as name, m.title as title
 order by name, title;