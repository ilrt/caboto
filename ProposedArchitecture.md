**This page is out of date and nonsense ... needs updating to reflect current state of project**


# Suggested architecture #

![http://caboto.googlecode.com/svn/wiki/caboto_architecture.png](http://caboto.googlecode.com/svn/wiki/caboto_architecture.png)

## Annotations Service ##

### Annotations War File ###

#### REST Web Services ####

A RESTFul service. We will use the emerging [JSR-311](http://jcp.org/en/jsr/detail?id=311) standard and thus [Jersey](https://jersey.dev.java.net/), the reference implementation. The URL's that invoke services will be fully documented and will remain the same if we decide to use a different REST framework, e.g. [RESTlets](http://www.restlet.org/).

#### JavaScript, Flex ... ####

The annotation service might be hosted on a different domain to that of the client that is using it. To avoid cross domain issues the service will provide JavaScript libraries and Flex components to create forms for adding simple annotations. These can be used by the client by linking to the JS or SWF file that is held on the remote annotation server.

#### Service Layer ####

A collection of Java interfaces and clients that are invoked by the "REST Web Services" layer. These will handle SPARQL queries etc. We will use Jena for the SPARQL query engine.

### RDF Datastore ###

A relational database to store the RDF - we will use [SDB](http://jena.sourceforge.net/SDB/) and [Derby](http://db.apache.org/derby/).

### Message Queue ###

A message queue might be a useful tool if we need to queue messages:

  * A user objects to a comment made by another user. The objection is stored on the queue until they are seen by an admin.
  * The system might have moderation switched on ... all messages need to be queued for approval.

Queue technologoies:

  * [ActiveMQ](http://activemq.apache.org/) from Apache.
  * [Amazon SQS](http://www.amazon.com/Simple-Queue-Service-home-page/b?ie=UTF8&node=13584001)