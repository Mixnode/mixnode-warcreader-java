package com.mixnode.warcreader;
import java.io.IOException;

@SuppressWarnings("serial")
public class WarcFormatException extends IOException {

	public WarcFormatException( String message ) {
		super( message );
	}

	public WarcFormatException( String message, Throwable e ) {
		super( message, e );
	}
}
