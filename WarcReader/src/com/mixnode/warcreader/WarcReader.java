package com.mixnode.warcreader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.io.input.BoundedInputStream;

import com.mixnode.warcreader.record.WarcContentBlock;
import com.mixnode.warcreader.record.WarcRecord;

public class WarcReader {
	protected final InputStream input;
	protected String charset = "US-ASCII";
	public WarcReader (InputStream compressedStream) throws IOException {
		input = new GZIPInputStream(compressedStream);
	}
	public WarcReader (InputStream compressedStream, String charset) throws IOException {
		input = new GZIPInputStream(compressedStream);
		this.charset = charset;
	}
	public WarcReader (InputStream stream, String charset, boolean compressed) throws IOException {
		if (compressed)
			input = new GZIPInputStream(stream);
		else
			input = stream;
		this.charset = charset;
	}
	WarcRecord lastRecord;
	public WarcRecord readRecord() throws IOException {
		if (lastRecord != null) {
			lastRecord.skip();
			HttpParser.readLine(input, "UTF-8");
			HttpParser.readLine(input, "UTF-8");
		}
		lastRecord = parse();
		return lastRecord;
	}
	protected WarcRecord parse() throws WarcFormatException {
		WarcRecord record;
		String protocol = null;
		try {
			protocol = HttpParser.readLine(input, charset);
		} catch (IOException e1) {
			throw new WarcFormatException("Illegal warc format");
		}
		if(protocol == null)
			return null;
		if (protocol.toLowerCase().startsWith("warc/") == false) {
			throw new WarcFormatException("Warc version is missing");
		}
		HeaderGroup headers = new HeaderGroup();
		try {
			headers.setHeaders(HttpParser.parseHeaders(input, charset));
		}
		catch (IOException e) {
			throw new WarcFormatException("cannot parse warc headers");
		}
		try {
		long payloadSize =Long.parseLong(headers.getFirstHeader("Content-Length").getValue());
		BoundedInputStream payload = new BoundedInputStream(input,payloadSize);
		record = new WarcRecord(headers,payload);
		}
		catch (NumberFormatException e) {
			throw new WarcFormatException("Cannot parse warc Content-Length");
		}
		return record;
	}

}
