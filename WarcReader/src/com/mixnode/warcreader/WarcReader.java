package com.mixnode.warcreader;

import com.mixnode.warcreader.record.WarcRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.io.input.BoundedInputStream;

/**
 * WarcReader class provides basic function to read and parse
 * a WARC file. Providing a compressed or uncompressed stream of WARC file,
 * WarcReader read WARC records and parse is to a WarcRecord object
 * @author Hadi Jooybar
 */
public class WarcReader {
	/**
	 * WARC stream from which all read and parse operations happen.
	 */
	protected final InputStream input;

	/**
	 * Charset used for parser
	 * Default value is "US-ASCII"
	 */
	protected String charset = "US-ASCII";

	/**
	 * Internal pointer to the last read WARC record
	 */
	WarcRecord lastRecord;

	/**
	 * Create a WarcReader object for a Compressed stream of a WARC file
	 * @param compressedStream Input compressed stream
	 * @throws IOException
	 */
	public WarcReader (InputStream compressedStream) throws IOException {
		input = new GZIPInputStream(compressedStream);
	}

	/**
	 * Create a WarcReader object for a Compressed stream of a WARC file
	 * with a specific charset for the parser
	 * @param compressedStream compressedStream Input compressed stream
	 * @param charset character set for the parser
	 * @throws IOException
	 */
	public WarcReader (InputStream compressedStream, String charset) throws IOException {
		input = new GZIPInputStream(compressedStream);
		this.charset = charset;
	}

	/**
	 * Create a WarcReader object for a stream.
	 * @param stream Input stream
	 * @param charset charset character set for the parser
	 * @param compressed whether the input stream is compressed
	 * @throws IOException
	 */
	public WarcReader (InputStream stream, String charset, boolean compressed) throws IOException {
		if (compressed)
			input = new GZIPInputStream(stream);
		else
			input = stream;
		this.charset = charset;
	}

	/**
	 * Read a WARC record from A WARC file
	 * By a call to this function WARC reader will pass the last record.
	 * This means that any stream from a WARC record will not be accessible
	 * after a new 'readRecord' call.
	 * @return a WARC record object
	 * @throws IOException
	 */
	public WarcRecord readRecord() throws IOException {
		if (lastRecord != null) {
			lastRecord.skip();
			HttpParser.readLine(input, charset);
			HttpParser.readLine(input, charset);
		}
		lastRecord = parse();
		return lastRecord;
	}

	/**
	 * Base on WARC format specification 'parse' function parses
	 * a WARC record and create a WarcRecord object
	 * This function throw WarcFomatException if the structure of input file
	 * is incorrect. Explanation for parse error is provided
	 * in WarcFomatException message
	 * @return Output WARC record
	 * @throws WarcFormatException
	 */
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
