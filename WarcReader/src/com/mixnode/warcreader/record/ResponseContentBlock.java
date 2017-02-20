package com.mixnode.warcreader.record;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.AbstractHttpMessage;

import com.mixnode.warcreader.WarcFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.CountingInputStream;

public class ResponseContentBlock extends AbstractHttpMessage implements HttpResponse, WarcContentBlock {
	private static final int BUFFER_SIZE = 1024;
	private final ProtocolVersion protocolVersion;
	private final StatusLine statusLine;
	private final HttpEntity entity;

	private ResponseContentBlock(final HttpResponse response) throws IOException {
		protocolVersion = response.getProtocolVersion();
		statusLine = response.getStatusLine();
		setHeaders( response.getAllHeaders() );
		entity = response.getEntity();
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}


	@Override
	public String toString() {
		return "\nResponse status line: " + this.statusLine +
			   "\nResponse headers: " + Arrays.toString( this.getAllHeaders());
	}

	public static ResponseContentBlock createWarcRecord(final BoundedInputStream stream) throws IOException {
		SessionInputBufferImpl buffer = new SessionInputBufferImpl( new HttpTransportMetricsImpl(), BUFFER_SIZE, 0, null, null );
		buffer.bind(stream);
		final DefaultHttpResponseParser responseParser = new DefaultHttpResponseParser( buffer );
		final HttpResponse response;
		try {
			response = responseParser.parse();
		} catch ( HttpException e ) {
			throw new WarcFormatException( "Can't parse the response", e );
		}
		final BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent( new IdentityInputStream(buffer) );
		Header contentTypeHeader = response.getFirstHeader( HttpHeaders.CONTENT_TYPE );
		if ( contentTypeHeader != null ) entity.setContentType( contentTypeHeader );
		response.setEntity( entity );

		return new ResponseContentBlock(response);
	}

	@Override
	public HttpEntity getEntity() {
		return entity;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public StatusLine getStatusLine() {
		return statusLine;
	}

	@Override
	public void setEntity(HttpEntity arg0) {
		
	}

	@Override
	public void setLocale(Locale arg0) {
		
	}

	@Override
	public void setReasonPhrase(String arg0) throws IllegalStateException {
		
	}

	@Override
	public void setStatusCode(int arg0) throws IllegalStateException {
		
	}

	@Override
	public void setStatusLine(StatusLine arg0) {
		
	}

	@Override
	public void setStatusLine(ProtocolVersion arg0, int arg1) {
		
	}

	@Override
	public void setStatusLine(ProtocolVersion arg0, int arg1, String arg2) {
		
	}

	@Override
	public InputStream payload() throws IOException {
		return getEntity().getContent();
	}

	@Override
	public void dump(File file) throws IOException {
		FileOutputStream out = new FileOutputStream (file);
		IOUtils.copy(payload(), out);
		out.close();
		
	}	
}
