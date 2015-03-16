### Scenarios ###

  1. John went to a talk that he found particularly interesting. He wanted to submit some comments and points about his own related work that other attendees might find interesting.
  1. Fred went to the keynote talk at an international symposium. He took a number pictures during the talk which he then subsequently updated to Flickr. He used the annotation system to make the pictures available to other delegates.
  1. Jane was particularly interested in some arguments made in a talk. She used the annotation system to leave some questions with the hope that the speaker, or other attendees, might provide some answers.
  1. During a talk on web technologies a parallel discussion by a number of delegates was occurring in an IRC channel. One of the delegates subsequently provided a link to the IRC logs.
  1. Fred reviews in his blog a paper he attended at a conference. The pingback functionality of the blog notifies the system of the blog entry and the annotation system links back to the blog entry. (Also support trackback?)
  1. Anne wanted to make some notes about a talk. However, she wanted to keep the comments personal.
  1. An organization had sponsored an event. They want the talk to be publicly available but only want comments to be available to others within the organization.

### Functionality ###

  * A common annotation is the ability to provide a link with a comment. It should also be possible to categorize the annotation; for example, a photo, link to a paper, an IRC log etc.
  * The ability to provide a numeric rating - 3 out of 5.
  * Provide folksomony tags to categorize an item.
  * The ability to make annotations as public, private and restricted. Those marked "public" are available to all; "private" are restricted to the author; and "restricted" are only available to a specified group or virtual organization.
  * Investigate the possibility of hooking into the pingback and trackback functionality of blogs to track blog entries and comments made outside of the annotations system.

### Security ###

  * Allow anonymous submissions. This will need some form of [CAPTCHA service](http://recaptcha.net/).
  * Support container-managed authentication. This will be useful for the service being used in a portal.
  * Support OpenID - useful for a site that doesn't maintain is own authentication system.

### Moderation ###

  * Allow the moderation of submissions by an administrator before they are made public.
  * Allow users of the system to mark submissions by others as objectionable. This will trigger a process such as notifying an administrator. The BBC has something similar in their ["Have Your Say"](http://www.bbc.co.uk/haveyoursay).

### API ###

  * A RESTful interface. Use [RESTlets](http://www.restlet.org/) or [Jersey](https://jersey.dev.java.net/) ([JSR-311](http://jcp.org/en/jsr/detail?id=311) implementation)?
  * UI components that interact with the service and display annotations. JSP tag libraries, JSF components, Flex?

### Schema ###

  * Use the [schema](http://www.w3.org/2000/10/annotation-ns#Annotea) with extensions where necessary.

### Storage ###

  * An RDF store that supports named graphs.
  * An abstraction layer that allows the support of different implementations of storage.
  * An implementation that supports [Jena's SDB](http://jena.hpl.hp.com/wiki/SDB).
  * An implementation that supports the [Talis](http://www.talis.com/platform/) platform.

### Date and Time ###

  * The submission date/time is stored with xsd:dateTime with the UTC time zone.
  * The date/time will appear in the user's UI with their locale time zone.

### Example ###

  * An example WAR file that uses the service.