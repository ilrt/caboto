# The RESTful Resources #

Below are the paths currently supported by Caboto (version 0.2).

  * **{uid}** - refers to the username of an individual
  * **{id}** - refers to a unique value (UUID) that is part of the URI that identifies an annotation.

| **Path** | **HTTP Method** | **Consume Types** | **Response Types** | **Description** |
|:---------|:----------------|:------------------|:-------------------|:----------------|
| `/about/?id={itemUri} `| GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all of the annotations that annotate the item that has {itemUri} as its identifier |
| `/person/{uid}/private/` | POST | application/x-www-form-urlencoded |  | Add a new private annotation |
| `/person/{uid}/private/` | GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all private annotations for the user **{uid}** |
| `/person/{uid}/private/{id}/` | GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return an individual private annotation |
| `/person/{uid}/private/{id}/` | DELETE |  | application/json, application/rdf+xml, text/rdf+n3 | Delete an individual private annotation |
| `/person/{uid}/public/` | POST | application/x-www-form-urlencoded |  | Add a new public annotation |
| `/person/{uid}/public/` | GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all public annotations for the user **{uid}** |
| `/person/{uid}/public/{id}/` | GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return an individual public annotation |
| `/person/{uid}/public/{id}/` | DELETE |  | application/json, application/rdf+xml, text/rdf+n3 | Delete an individual public annotation |
| `/about/?id={itemUri} `| GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all of the annotations that annotate the item that has {itemUri} as its identifier |
| `/about/?{ns:prop}={literal|U:uri}+ `| GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all of the annotations where the body has ns:prop with value literal or uri |
| `/about/?search={searchterm} `| GET |  | application/json, application/rdf+xml, text/rdf+n3 | Return all of the annotations matching searchterm |
| `/version/`| GET |  | text/plain | Return the caboto version number |
| `/query/annotations?query=`| GET |  | application/xml | Query annotations using sparql |
| `/query/relations?query=`| GET |  | application/xml | Query annotations as dereified relations using sparql |