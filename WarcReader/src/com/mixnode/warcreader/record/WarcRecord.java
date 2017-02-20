package com.mixnode.warcreader.record;
import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.io.input.BoundedInputStream;

/**
 * Basic constituent of a WARC file, consisting of a sequence of WARC records.
 * Class WarcRecord contains all information of a WARC record.
 * WarcRecord consists of
 * <ul>
 * <li> Protocol of the WARC record
 * <li> WARC record headers
 * <ul>
 * <li> WARC-Type
 * <li> WARC-Record-ID
 * <li> WARC-Date
 * <li> Content-Length
 * </ul>
 * <li> WARC record content block
 * </ul>
 * @author Hadi Jooybar
 */
public class WarcRecord {
	/**
	 * WarcType specifies the type of a WARC record.
	 * 'WARC-Type' field is mandatory for all WARC records
	 * WARC records unrecognized type will cause an exception
	 */
	public enum WarcType {
		/**
		 * 'warcinfo record contains some information about following WARC records
		 */
		warcinfo ("warcinfo"),
		/**
		 * Request WARC record contains a complete scheme specific (HTTP, HTTPS, etc.) request
		 */
		request ("request"),
		/**
		 * Response WARC record contains a scheme-specific response.
		 * The most common use if "response" is for HTTP/HTTPS response
		 */
		response ("response"),
		/**
		 * Resource WARC record contains a resource without HTTP/HTTPS wrapping 
		 */
		resource("resource"),
		/**
		 * Metadata WARC record usually describes feature of another WARC-Record specified by
		 * 'WARC-Concurrent-To header' or 'WARC-Refers-To' WARC headers
		 */
		metadata("metadata"),
		revisit("revisit"),
		conversion("conversion"),
		segmentation("segmentation");
		WarcType( final String value ) {
		}
	}
	
	/**
	 * WARC version number. 'protocol' string format is WARC/X,
	 * in which X is the version of WARC record
	 */
	protected String protocol;

	/**
	 * WARC-Type of the WARC record
	 */
	protected WarcType type;

	/**
	 * warcHeaders is a container of all Headers of a WARC record.
	 */
	protected HeaderGroup warcHeaders;

	/**
	 * warcContentBlock is a base interface for all implementations of
	 * different content blocks. warcContentBlock may refer to different
	 * objects base on WARC-Type
	 * @see type
	 */
	protected WarcContentBlock warcContentBlock;

	/**
	 * Returns WARC content block of a WARC record
	 * warcContentBlock is a base interface for all implementations of
	 * different content blocks. Returned warcContentBlock may refer to different
	 * objects base on WARC-Type
	 * @return WarcContentBlock object of a WARC record
	 */
	public WarcContentBlock getWarcContentBlock() {
		return warcContentBlock;
	}

	/**
	 * Create a WarcRecord object and initialize WARC headers
	 * Content block of the WarcRecord is null
	 * @param warcHeaders WARC headers of a WARC record
	 */
	public WarcRecord(final HeaderGroup warcHeaders) {
		this(warcHeaders,null);
	}

	/**
	 * Default constructor
	 * WARC headers is empty and WARC content block is null 
	 */
	public WarcRecord() {
		this(null, null);
	}

	/**
	 * Creates a WARC record with specified WARC Headers.
	 * @param warcHeaders WARC Headers of the WARC record
	 * @param contentBlockStream Content block stream 
	 */
	public WarcRecord(final HeaderGroup warcHeaders, BoundedInputStream contentBlockStream) {
		this.warcHeaders = warcHeaders == null ? new HeaderGroup() : warcHeaders;
		if (warcHeaders != null)
			type = WarcType.valueOf(warcHeaders.getFirstHeader("WARC-Type").getValue().toLowerCase());
		this.stream = contentBlockStream;
		try {
			if (type == WarcType.response) {
				warcContentBlock = ResponseContentBlock.createWarcRecord(contentBlockStream);
			}
			else if (type == WarcType.request) {
				warcContentBlock = RequestContentBlock.createWarcRecord(contentBlockStream);
			}
			else
				warcContentBlock = new DefaultContentBlock(contentBlockStream);
		} catch (IOException e) {
			System.out.println("WARNING: cannot parse content block of WARC record " + 
					warcHeaders.getFirstHeader("WARC-Record-ID").getValue());
		}
	}

	/**
	 * Returns a HeaderGroup containing all headers of a WARC record
	 * @return WARC record Headers
	 */
	public HeaderGroup getWarcHeaders() {
		return warcHeaders;
	}

	/**
	 * Set headers of a WARC record
	 * @param warcHeaders HeaderGroup object
	 */
	public void setWarcHeaders(HeaderGroup warcHeaders) {
		this.warcHeaders = warcHeaders;
	}

	/**
	 * Returns WARC-Type of a WARC record
	 * @return WARC-Type
	 * @see type
	 */
	public WarcType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "\nWarcHeaders: " + warcHeaders.toString() +
				stream.toString();
	}

	/**
	 * Skip remaining part of a WARC record stream.
	 * This function must be called before reading a new WARC record.
	 * Content block stream will not accessible after calling this function.
	 * Best practice is to call skip after finish processing of a WARC record
	 * @throws IOException
	 */
	public void skip() throws IOException {
		stream.skip(Long.MAX_VALUE);
	}

	/**
	 * Returns WARC-Record-ID of a WARC record.
	 * WARC-Record-ID is An identifier assigned to the current record 
	 * that is globally unique for intended amount of time
	 * WARC-Record-ID is a mandatory field of record WARC header
	 * @return WARC-Record-ID string if possible. Returns null when WARC headers
	 * does not contain WARC-Record-ID field
	 */
	public String getRecordID() {
		if (warcHeaders.containsHeader("WARC-Record-ID"))
			return warcHeaders.getFirstHeader("WARC-Record-ID").getValue();
		return null;
	}

	/**
	 * Mixnode WARC records may provide a guessed charset for a WARC record
	 * @return Guessed charset if exists, otherwise null.
	 */
	public String guessedCharSet() {
		if (warcHeaders.containsHeader("Mixnode-Guessed-Charset"))
			return warcHeaders.getFirstHeader("Mixnode-Guessed-Charset").getValue();
		return null;
	}

	/**
	 * Add a header to a HeaderGroup if it doesn't exist.
	 * @param headers Destination header group
	 * @param name New Header name
	 * @param value New Header Value
	 */
	public static void addIfNotPresent( final HeaderGroup headers, final String name, final String value ) {
		if ( ! headers.containsHeader( name ) ) headers.addHeader( new Header( name, value ) );
	}

	/**
	 * Internal input stream which is being use to create content block.
	 */
	private BoundedInputStream stream;
}
