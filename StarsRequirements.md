### Scenarios ###
  1. Anne views a resource and wants to add a textual comment about it.
  1. Anne then views another resource and embeds an image from flickr in her comment, along with an external link.
  1. Bob thinks it would useful to state that a resource is related to another resource within the system. He picks a property from the list to describe the relationship and asserts it.
  1. Bob sees a comment by Anne and agrees with it. He clicks a button to '+1' it.
  1. Anne sees the relationship added by Bob and disagrees with it. She clicks a button to '-1' it.
  1. Anne wants to assert a relationship between two resources but is not happy with the choices offered by the list. She creates a new property relation and uses that instead.
  1. Bob is offended by a comment he has found. He clicks a button to report it to an administrator.
  1. An administrator wants to remove a comment that is offensive. He logs into his admin account, navigates to the comment and clicks a button to hide it.
  1. An administrator wants to delete a spam comment. He clicks a button to delete it entirely.
  1. Anne records a time-based annotation about a piece of video.
  1. People: Displaying Nikki's details reveals that Jasper has claimed that Mike knows her. We can currently show annotations that are about Nikki, but not those that have Nikki as a value of an annotation entry.
  1. Tags: Find all things that are tagged with "tag". This would require Caboto returning all annotations (of type TagAnnotation) that have a particular entry/value combination.
  1. Media (1): Find all videos with a bridge in. Amounts to: find all annotations (of type VideoAnnotation) where 'bridge' occurs in the value of some particular entry. A free text search one.
  1. Media (2): Find all videos that feature Bristol. Amounts to: find all annotations (of type VideoAnnotation) where 'Bristol' _is_ the value of some location-type entry. The value 'Bristol' originates from some constrained pick-list used in the original annotation creation UI.

### Security and Authorisation ###
  1. Only logged-in users may create annotations.
  1. Annotations are always related visually to the user who created them.
  1. Should support external authentication provision.

### Display ###
  1. Users should be able to switch off the display of user annotations (maybe per-user?).
  1. Annotations should be included in the search capacity of the enclosing site.

### Export ###
  1. Annotation data should be available to be exported (e.g. by a SPARQL endpoint) as part of the resource being annotated.