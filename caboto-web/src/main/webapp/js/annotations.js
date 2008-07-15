var META_TAG_CABOTO = "caboto-annotation";
var APPLICATION_JSON = "application/json"
var ANNO_BASE;

function initializeAnnotations() {
	
	ANNO_BASE = findBase();
	
     // hides the js support message..
     displayMessage("<p>Loading annotations..</p>");

     // enable the form
     clearForm();

     // get the annotations
     findAnnotations();
}

/*
 * Find where this script came from
 */

function findBase() {
	var scripts = document.getElementsByTagName("SCRIPT");
	for (var i = 0; i < scripts.length; i++) {
		var aSrc = scripts[i].getAttribute("src");
		if (aSrc != null && 
				aSrc.search(/js\/annotations.js$/) != -1) {
			return aSrc.replace(/js\/annotations.js$/, "");
		}
	}
	return "";
}

/*
     Ajax call - find annotations that are about the "uri".
  */
function findAnnotations() {

     var annoUri = "";

     // find the URI of the thing we are interested in
     var meta = document.getElementsByTagName("META");

     for (var i = 0; i < meta.length; i++) {
         if (meta[i].getAttribute("name") == META_TAG_CABOTO) {
             if (meta[i].getAttribute("content") != null) {
                 annoUri = meta[i].getAttribute("content");
             }
         }
     }

     if (annoUri.length > 0) {
         new Ajax.Request(ANNO_BASE + './annotation/about/', {
             method:'get',
             parameters: createParams(annoUri),
             requestHeaders: {Accept: APPLICATION_JSON},
             onSuccess: displayAnnotations,
             onFailure: failure
         });


     } else {
         document.getElementById("annotations-results").innerHTML =  
"<p>Something has gone wrong! "
                 + "The system is unable to determine the ID of the  item that can be "
                 + "annotated.</p>"
     }

}

function createParams(uri) {

     var params = "id=" + encodeURIComponent(uri);

     // extra parameter to stop IE caching the ajax requests
     if (Prototype.Browser.IE) {
         params += "&ie-cache-hack=" + new Date().getTime();
     }

     return params;
}

function displayMessage(message) {
     document.getElementById("annotations-results").innerHTML = message;
}

function failure(failure) {

     var error;

     if (failure.status == 404) {
         error = "<p>There are currently no annotations.</p>";
     } else if (failure.status == 401) {
         error = "<p>You are not authorized to view the annotations.</p>"
     } else {
         error = "<p>" + failure.status + " - something has gone wrong!"
     }

     displayMessage(error);
}


/*
     Display annotations - they are received in JSON format.
  */
function displayAnnotations(transport) {

     var uid = getCookie("uid");
     var admin = getCookie("admin");

     var json = transport.responseText.evalJSON(true);

     var output = "";

     if (json.length > 0) {

         json.sort(function(a, b) {
             return parseDate(a.created) - parseDate(b.created)
         });

         for (var i = 0; i < json.length; i++) {
             output += formatAnnotation(json[i], uid, admin);
         }
     } else {
         output = "<p>There are no annotations.</p>";
     }


     displayMessage(output);
     clearForm();
}

function getCookie(name) {

     var cookie = null;

     var allCookies = document.cookie;

     var cookies = allCookies.split(";");

     for (var i = 0; i < cookies.length; i++) {

         var tmp = cookies[i].replace(/^\s+|\s+$/g, '').split("=");

         if (tmp[0] == name) {
             cookie = tmp[1];
             break;
         }
     }

     return cookie;
}


function formatAnnotation(annotation, uid, admin) {

     var date = parseDate(annotation.created);
     var author = parseAuthor(annotation.author);
     var body = annotation.body.description.replace(/\n|\r/g, "<br />\n");

     var output = "<div class='annotation-entry'>"
             + "<div class='annotation-entry-title'>" +  
annotation.body.title + "</div>"
             + "<div class='annotation-entry-description'>" + body +  
"</div>"
             + "<div><div class='annotation-entry-created'>"
             + "Created on " + date.toLocaleString() + " by " + author  
+ "</div>"

     if (uid == author || admin == "true") {
         output += "<div style='float: right'>[<a href='#' onclick='deleteAnnotation(\""
                 + annotation.id + "\")'>Delete</a>]</div>";
     }

     output += "</div>"

     output += "</div>";


     //output += "<div><div style='float: left'>a</div><div style='float: right'>b</div></div>"

     return output;
}

/*
     Parses date strings that are in xsd:dateTime format.
  */
function parseDate(dateString) {

     var d = new Date(dateString.substr(0, 19).replace(/-/g,  
'/').replace("T", " "));

     d.setMinutes(d.getMinutes() + d.getTimezoneOffset());

     var timeZone = dateString.substr(19).split(":");
     timeZone = timeZone[0] * 60 + (timeZone[1] * 1);
     d.setMinutes(d.getMinutes() + timeZone);

     return d;
}

/*
     Takes the URI that represents the person and returns just the  
username portion.
  */
function parseAuthor(author) {
     var temp = author.split("/");
     return temp[temp.length - 2];
}

function processForm(uri) {

     var message = "";
     
     uri = ANNO_BASE + uri;

     if (!Form.Element.present("annotation-title")) {
         message += "You need to provide a title. ";
     }

     if (!Form.Element.present("annotation-body")) {
         message += "You need to provide a body.";
     } else {

         var body = Form.Element.getValue("annotation-body");

         // <http://haacked.com/archive/2004/10/25/usingregularexpressionstomatchhtml.aspx>
         var matches = body.match(/<\/?\w+((\s+\w+(\s*=\s*(?:\".*?\"|\'.*?\'|[^\'\">\s]+))?)+\s*|\s*)\/?>/);

         if (matches != null) {
             message += "It looks like you have markup in your comment - not supported, sorry!"
         }
     }


     if (message.length > 0) {
         document.getElementById("annotation-messages").innerHTML =  
"<p>" + message + "</p>";
         return;
     }

     // serialize the form data
     var s = Form.serialize("annotation-comment-form", true);

     // we don't need to send the submit
     delete s.submit;

     // send the form details
     new Ajax.Request(uri, {
         method:'post',
         parameters: s,
         onSuccess: findAnnotations,
         onFailure: function(transport) {
             alert(transport.responseText);
         }
     });

}

function clearForm() {
     if (document.getElementById('annotation-comment-form')) {
         if (getCookie("uid")) {
        	 Form.enable('annotation-comment-form');
        	 Form.Element.clear('annotation-title');
        	 Form.Element.clear('annotation-body');
        	 Form.disable('openid-form');
        	 $('openid-form').setStyle('display: none;');
         }
     }
}

function deleteAnnotation(uri) {

     new Ajax.Request(uri, {
         method:'DELETE',
         onSuccess: initializeAnnotations,
         onFailure: function(n) {
             alert("Failed!");
         }
     });
}

