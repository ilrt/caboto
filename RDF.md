### Graphs ###

Each person will have at least two graphs to store their annotations:

  1. A graph to store their public annotations.
  1. A graph to store their private annotations.

In addition, a person might belong to virtual organizations and groups. Each group will have a graph for annotations that can be viewed by members of that group ( - is this a CREW-specific thing)? _At present we don't have any groups management requirement in STARS - jasper_

The name of the graphs should be unique and help determine provenance. In addition, to help establish if the graphs are public, private or group the URI should include an appropriate identifiable string, i.e. "public", "private" and "group".

For example:

  * `http://cabato.org/annotations/person/mikejones/public`
  * `http://cabato.org/annotations/person/mikejones/private`
  * `http://cabato.org/annotations/group/web_futures`

### Schemas ###

[Annotea](http://www.w3.org/2000/10/annotation-ns#Annotea) seems to be a good starting point. Possible extensions:
  * Something to support simple 'plus' or 'minus' voting. E.g. '10 people found this useful'. In RDF, in a user's annotation graph
```
cab:annotation_uri1 annotea:hasAnnotation cab:annotation_uri2 .
cab:annotation_uri2 cab:isPositive "true"
```
  * ...

### URIs ###

The system will need to mint URIs for the following:

  1. A person.
  1. A group.
  1. The graph that holds a person's public annotations.
  1. The graph that holds a person's private annotations.
  1. The graph that holds a group's annotations.
  1. A public annotation created by a person.
  1. A private annotation created by a person.
  1. An annotation created by a person for a group.

The URIs will be constructed in the follow manner:

| **Description** | **URI** | **Example** |
|:----------------|:--------|:------------|
| A person | ` {host.name}/{web.context}/person/{uid} ` | ` http://cabato.org/annotations/person/mikejones ` |
| A group | ` {host.name}/{web.context}/group/{gid} ` | ` http://cabato.org/annotations/group/web_futures ` |
| A named graph that holds a person's public annotations | ` {host.name}/{web.context}/person/{uid}/public ` | ` http://cabato.org/annotations/person/mikejones/public ` |
| A named graph that holds a person's private annotations | ` {host.name}/{web.context}/person/{uid}/private ` | ` http://cabato.org/annotations/person/mikejones/private ` |
| A named graph that holds annotations for a virtual group | ` {host.name}/{web.context}/group/{gid}/ ` | ` http://cabato.org/annotations/group/web_futures ` |
| A public annotation created by a person | ` {host.name}/{web.context}/person/{uid}/public/{id} ` | ` http://cabato.org/annotations/person/mikejones/public/#20080315-ae527hgj ` |
| A private annotation created by a person | ` {host.name}/{web.context}/person/{uid}/private/{id} ` | ` http://cabato.org/annotations/person/mikejones/private/#20080315-ae547hgh ` |
| A group annotation created by a person | ` {host.name}/{web.context}/group/{gid}/{id} ` | ` http://cabato.org/annotations/group/web_fuures/#20080315-ae523ghgk` |

**Note:** How do we create the unique ID for the URIref? Create a [UUID](http://java.sun.com/j2se/1.5.0/docs/api/java/util/UUID.html) that is then [Base64](http://iharder.sourceforge.net/current/java/base64/Base64) encoded.

### Data ###

#### Person ####

When the annotation is displayed in the UI it will need to display the basic details about the person who made that annotation. Some things need to be considered here:

  * If the client application holds user details we don't want to duplicate this information on the annotation application, otherwise we will have synchronization issues.
  * If we allow unauthenticated comments, or authentication via openid then we will need a mechanism for capturing user details and storing them on the annotation application. They can be captured via the form used to add the annotation.

Possible solutions:

  * The annotation application just notes the uid of the account of the person and stores that and returns that information with any annotation. The client application must resolve the uid to something meaningful that can be displayed in the UI.
  * There is a callback mechanism from the annotation application back to the client applicatiob to obtain the details of the user.
  * Unauthenticated or OpenId authenticated annotations will need user details to be added via the comment form.


So, where the system is being integrated with a client application that stores the authentication details, we just store the uid:

```
<foaf:Person rdf:about="http://caboto.org/annotations/person/mikej">
    <cabato:holdsPrincupal>mikej<cabato:holdsPrincupal>
</foaf:Person>
```

The annotation is responsible for minting the URI based in information it receives from the client application, i.e. the uid of the user. This information might be passed in a hidden field in the form for submitting the annotation.

If the details had been submitted via the comment form then the annotation application might have fuller details stored:

```
<foaf:Person rdf:about="http://caboto.org/annotations/person/mikejones">
    <foaf:name>Mike Jones</foaf:name>
    <foaf:homepage rdf:resource="http://chillyinside.com/blog" />
    <rdfs:seeAlso rdf:resource="http://chillyinside.com/foaf.rdf"/>
    <foaf:openid rdf:resource="http://openid.chillyinside.com"/>
</foaf:Person>
```

#### Group ####

We need a URI to represent groups - I'm assuming that these details will be passed from the client application to the annotation application ... needs more thought!

```
<foaf:Group rdf:about="http://caboto.org/annotations/group/web_futures">
    <foaf:name>Web Futures</foaf:name>
</foaf:Group>
```


#### Annotations ####

A simple comment:

```
<caboto:SimpleComment rdf:about="http://cabato.org/annotations/person/mikejones/public/#20080315-ae527hgj">
    <annotea:annotates rdf:resource="http://www.crew-vre/net/events/1225"/>
    <annotea:author rdf:resource="http://cabato.org/annotations/person/mikejones"/>
    <annotea:created>2008-03-15T14:10:22Z</annotea:created>
    <annotea:body rdf:parseType="Resource">
        <dc:title >A title to my annotations</dc:title>
        <dc:description>A comment goes here</dc:description>
    </annotea:body>
</caboto:SimpleComment>
```

An annotation with a link:

```
<caboto:LinkedComment rdf:about="http://cabato.org/annotations/person/mikejones/public/#20080315-ae167aghh">
    <annotea:annotates rdf:resource="http://www.crew-vre/net/events/1225"/>
    <annotea:author rdf:resource="http://cabato.org/annotations/person/mikejones"/>
    <annotea:created>2008-03-15T14:10:22Z</annotea:created>
    <annotea:body rdf:parseType="Resource">
        <caboto:linkToItem rdf:resource="http://example.org/goodstuffhere.html" />
        <dc:title >A title to my annotations</dc:title>
        <dc:description>A comment goes here</dc:description>
    </annotea:body>        
</caboto:LinkedComment>
```

An annotation with a link to a photo:

```
<caboto:PhotoComment rdf:about="http://cabato.org/annotations/person/mikejones/public/#20080315-ae123gajhga">
    <annotea:annotates rdf:resource="http://www.crew-vre/net/events/1225"/>
    <annotea:author rdf:resource="http://cabato.org/annotations/person/mikejones"/>
    <annotea:created>2008-03-15T14:10:22Z</annotea:created>
    <annotea:body rdf:parseType="Resource">
        <caboto:linkToPhoto rdf:resource="http://example.org/greatpic.jpg" />
        <dc:title >A title to my annotations</dc:title>
        <dc:description>A comment about the photo here.</dc:description>
    </annotea:body>        
</caboto:LinkedComment>
```

Mike wanted a generic _link_ property, like an href. Damian's brain wasn't working, but DC terms provides [references](http://dublincore.org/documents/dcmi-terms/#terms-references) for this very purpose. DC provides....

#### Crew Video Live Annotations ####

```
<crew:LiveAnnotation rdf:about="http://caboto.org/annotations/person/tobiasschiebeck/public/#20080315-ae167aghh">
    <annotea:annotates rdf:resource="http://www.crew-vre/net/events/1225"/>
    <annotea:author rdf:resource="http://cabato.org/annotations/person/tobiasschiebeck"/>
    <annotea:created>2008-03-15T14:10:22Z</annotea:created>
    <annotea:body rdf:parseType="Resource">
        <dc:type>type of the liveAnnotation {question|answer|comment|link|reference|important|slide|person}</dc:type>
        <dc:title>name of person/title of the of link/reference</dc:title>
        <dc:description>A comment/content of the annotation</dc:description>
        <crew:url>http://example.org/goodstuffhere.html</crew:url>
        <crew:email>name@somewhere.org</crew:email>
     </annotea:body>        
</crew:LiveAnnotation>
```