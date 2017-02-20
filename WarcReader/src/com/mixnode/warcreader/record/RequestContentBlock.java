package com.mixnode.warcreader.record;

import com.mixnode.warcreader.WarcFormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.message.AbstractHttpMessage;

public class RequestContentBlock extends AbstractHttpMessage implements HttpRequest, WarcContentBlock {
	private static final int BUFFER_SIZE = 1024;
	private final ProtocolVersion protocolVersion;
	private final RequestLine requestLine;

	protected InputStream payload;
	private RequestContentBlock(final HttpRequest request, InputStream payload) throws IOException {
		protocolVersion = request.getProtocolVersion();
		requestLine = request.getRequestLine();
		setHeaders( request.getAllHeaders() );
		this.payload = payload;
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public String toString() {
		return "\nRequest request line: " + this.requestLine +
			   "\nRequest headers: " + Arrays.toString( this.getAllHeaders());
	}

	public static RequestContentBlock createWarcRecord(final BoundedInputStream stream) throws IOException {
		SessionInputBufferImpl buffer = new SessionInputBufferImpl( new HttpTransportMetricsImpl(), BUFFER_SIZE, 0, null, null );
		buffer.bind(stream);
		final DefaultHttpRequestParser requestParser = new DefaultHttpRequestParser( buffer );
		final HttpRequest request;
		try {
			request = requestParser.parse();
		} catch ( HttpException e ) {
			throw new WarcFormatException( "Can't parse the request", e );
		}
		return new RequestContentBlock(request, new IdentityInputStream(buffer));
	}


	@Override
	public InputStream payload() throws IOException {
		return payload;
	}

	@Override
	public void dump(File file) throws IOException {
		FileOutputStream out = new FileOutputStream (file);
		IOUtils.copy(payload(), out);
		out.close();
	}

	@Override
	public RequestLine getRequestLine() {
		return requestLine;
	}	
}
