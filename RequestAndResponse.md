## Below are just some ideas about the RESTFUL interface ... ##

### Web Application Context ###

Where ` {web.context} ` appears, this indicates a place holder for the application context of the cabato annotations war file. For example, a war file with the name `annotations.war` might have the context `/annotations/`.

### URIs to call services ###

The URIs in the table below do not include the context of the web application. So, if we have a context of `/annotations/` on ` http://caboto.org ` then the URI `/` specified in the table below, would actually be a request to `http://cabato.org/annotations/`.

| **URL** | **HTTP Method** | **Description** | **Notes** |
|:--------|:----------------|:----------------|:----------|
| ` /person/ ` | POST | Adds a new person to the system |  |
| ` /person/{uid}/ ` | GET | Returns the details of the person with the uid |  |
| ` /person/{uid}/ ` | PUT | Update the person's details |  |
| ` /person/{uid}/ ` | DELETE | Delete a person | This will need to delete the annotations created by the person as well |
| ` /group/ ` | POST | Adds a new group to the system |  |
| ` /group/{gid}/ ` | GET | Returns the details of the group with the gid |  |
| ` /group/{gid}/ ` | PUT | Update the group's details |  |
| ` /group/{gid}/ ` | DELETE | Delete a group | This will need to delete the annotations for the group as well |
| ` /person/{uid}/public/ ` | POST | Add a new public annotation for the person with the uid |  |
| ` /person/{uid}/public/{id}/ ` | GET | Get the public annotation identified by the id |  |
| ` /person/{uid}/public/{id}/ ` | PUT | Update the pubic annotation | Might  be used to withdraw an annotation |
| ` /person/{uid}/public/{id}/ ` | DELETE | Delete the public annotation |  |
| ` /person/{uid}/private/ ` | POST | Add a new private annotation for the person with the uid |  |
| ` /person/{uid}/private/{id}/ ` | GET | Get the private annotation identified by the id |  |
| ` /person/{uid}/private/{id}/ ` | PUT | Update the private annotation identified by the id  | Might  be used to withdraw an annotation |
| ` /person/{uid}/private/{id}/ ` | DELETE | Delete the private annotation identified by the id |  |
| ` /group/{gid}/restricted/ ` | POST | Add a new group annotation |  |
| ` /group/{gid}/restricted/{id} ` | GET | Get the group annotation identified by the id |  |
| ` /group/{gid}/restricted/{id} ` | PUT | Update the group annotation identified by the id  | Might  be used to withdraw an annotation |
| ` /group/{gid}/restricted/{id} ` | DELETE | Delete the group annotation identified by the id |  |
| ` /about/{id} ` | GET | Get all annotations that are about the annotation identified by the id |  |
| ` /sparql ` | POST | A SPARQL end point | Allow SemWeb clients to query the data store directly with SPARQL. The results will need to be filtered by the service to ensure that restricted resources are not returned. |

**TO DO:-**

  * Document the expected response codes that you will get from a request.
  * Document the content type returned - json, xml, rdf/xml.