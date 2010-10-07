package org.caboto.domain;

public class AnnotationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnnotationException() {
	}

	public AnnotationException(String message) {
		super(message);
	}

	public AnnotationException(Throwable cause) {
		super(cause);
	}

	public AnnotationException(String message, Throwable cause) {
		super(message, cause);
	}

}
