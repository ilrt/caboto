## Introduction ##

A quick quide to compiling and running Caboto.

## Prerequisites ##

You will need the subversion client and Maven 2 installed.

## Obtaining the source code ##

The most stable versions will be tagged as a "release". The latest release is "release\_0\_3".

Checkout the project from the Subversion repository:

`svn checkout http://caboto.googlecode.com/svn/tags/release_0_3/ caboto`

## Compiling Caboto ##

Compile the project:

`cd caboto/Caboto/`

`mvn install`

## Running the sample applications ##

Run the test web application:

`cd caboto-web/`

`mvn jetty:run`

The web application will now be available on port 9090.

## Adding a receiving annotations via a web browser ##

Point your web browser at http://localhost:9090/caboto-web/. The displayed web page includes the details of a number of test accounts that you can use to create annotations.

## Adding and receiving annotations via curl ##

Check the version of caboto that is being used:

`curl http://localhost:9090/caboto-web/annotation/version/`

The Response will be:

`0.2`

Create an annotation as "mike":

`curl -i -X POST http://mike:potato@localhost:9090/caboto-web/annotation/person/mike/public/ -d 'title=A%20Title&description=A%20description&type=SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing'`

The response will look like:

```
HTTP/1.1 201 Created
Location: http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745
Expires: Thu, 01 Jan 1970 00:00:00 GMT
Set-Cookie: JSESSIONID=vf2o0s317k04;Path=/caboto-web
Content-Length: 0
Server: Jetty(6.1.12rc1)
```


Request the newly created URI as RDF/XML:

`curl -i -X GET -H accept:application/rdf+xml http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745`

The following is returned:

```
HTTP/1.1 200 OK
Content-Type: application/rdf+xml
Transfer-Encoding: chunked
Server: Jetty(6.1.12rc1)

<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:caboto="http://caboto.org/schema/annotations#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:annotea="http://www.w3.org/2000/10/annotation-ns#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
  <caboto:SimpleComment rdf:about="http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745">
    <annotea:created rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >2008-09-08T13:42:32+01:00</annotea:created>
    <annotea:annotates rdf:resource="http://example.org/thing"/>
    <annotea:author rdf:resource="http://localhost:9090/caboto-web/annotation/person/mike/"/>
    <annotea:body>
      <rdf:Description rdf:about="http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745#body">
        <dc:title rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
        >A Title</dc:title>
        <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
        >A description</dc:description>
      </rdf:Description>
    </annotea:body>
  </caboto:SimpleComment>
</rdf:RDF>
```

You can request the same resource as N3 and JSON:

`curl -i -X GET -H accept:text/rdf+n3 http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745`

`curl -i -X GET -H accept:application/json http://localhost:9090/caboto-web/annotation/person/mike/public/feb5cba4-24fc-4825-8f92-ac8e51b6e745`

Add a private annotation as "damian":

`curl -i -X POST http://damian:carrot@localhost:9090/caboto-web/annotation/person/damian/private/ -d 'title=A%20Title&description=A%20description&type=SimpleComment&annotates=http%3A%2F%2Fexample.org%2Fthing'`

Get the annotations that refer to `http://example.org/thing` as JSON. View them as either an anoymous user or as "mike":

`curl -X GET -H accept:application/json http://localhost:9090/caboto-web/annotation/about/?id=http%3A%2F%2Fexample.org%2Fthing`

`curl -X GET -H accept:application/json http://mike:potato@localhost:9090/caboto-web/annotation/about/?id=http%3A%2F%2Fexample.org%2Fthing`

The response will only include the public annotation that was made by the "mike" user:

```
[{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/public\/feb5cba4-24fc-4825-8f92-ac8e51b6e745",
"type":"SimpleComment","created":"2008-09-08T13:42:32+01:00","annotates":"http:\/\/example.org\/thing",
"author":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/",
"body":{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/public\/feb5cba4-24fc-4825-8f92-ac8e51b6e745#body",
"title":"A Title","description":"A description"}}]
```

The same request made by the "damian" user will include that user's private annotation:

`curl -X GET -H accept:application/json http://damian:carrot@localhost:9090/caboto-web/annotation/about/?id=http%3A%2F%2Fexample.org%2Fthing`

```
[{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/damian\/private\/b45cfd14-eb93-4c22-820c-4cfa7d62394d",
"created":"2008-09-08T13:44:09+01:00","type":"SimpleComment","annotates":"http:\/\/example.org\/thing",
"body":{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/damian\/private\/b45cfd14-eb93-4c22-820c-4cfa7d62394d#body",
"title":"A Title","description":"A description"},"author":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/damian\/"},
{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/public\/feb5cba4-24fc-4825-8f92-ac8e51b6e745",
"type":"SimpleComment","created":"2008-09-08T13:42:32+01:00","annotates":"http:\/\/example.org\/thing",
"author":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/",
"body":{"id":"http:\/\/localhost:9090\/caboto-web\/annotation\/person\/mike\/public\/feb5cba4-24fc-4825-8f92-ac8e51b6e745#body",
"title":"A Title","description":"A description"}}]
```