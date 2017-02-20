package com.mixnode.warcreader.record;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.io.input.BoundedInputStream;


public class WarcRecord {
	public enum WarcType {
		warcinfo ("warcinfo"),
		request ("request"),
		response ("response"),
		resource("resource"),
		metadata("metadata"),
		revisit("revisit"),
		conversion("conversion"),
		segmentation("segmentation");
		WarcType( final String value ) {
		}
	}
	protected String ptotocol;
	protected WarcType type;
	protected HeaderGroup warcHeaders;
	protected WarcContentBlock warcContentBlock;

	public WarcContentBlock getWarcContentBlock() {
		return warcContentBlock;
	}
	public WarcRecord(final HeaderGroup warcHeaders) {
		this(warcHeaders,null);
	}
	public WarcRecord() {
		this(null, null);
	}
	public WarcRecord(final HeaderGroup warcHeaders, BoundedInputStream payload) {
		this.warcHeaders = warcHeaders == null ? new HeaderGroup() : warcHeaders;
		if (warcHeaders != null)
			type = WarcType.valueOf(warcHeaders.getFirstHeader("WARC-Type").getValue().toLowerCase());
		this.stream = payload;
		if (type == WarcType.response) {
			try {
				warcContentBlock = ResponseContentBlock.createWarcRecord((BoundedInputStream)payload);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (type == WarcType.request) {
			try {
				warcContentBlock = RequestContentBlock.createWarcRecord((BoundedInputStream)payload);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			warcContentBlock = new DefaultContentBlock(payload);
	}
	public HeaderGroup getWarcHeaders() {
		return warcHeaders;
	}
	public void setWarcHeaders(HeaderGroup warcHeaders) {
		this.warcHeaders = warcHeaders;
	}
	
	public WarcType getType() {
		return type;
	}
	@Override
	public String toString() {
		return "\nWarcHeaders: " + warcHeaders.toString() +
				stream.toString();
	}
	public void skip() throws IOException {
		stream.skip(Long.MAX_VALUE);
	}
	public String getRecordID() {
		return warcHeaders.getFirstHeader("WARC-Record-ID").getValue();
	}
	public String guessedCharSet() {
		return warcHeaders.getFirstHeader("Mixnode-Guessed-Charset").getValue();
	}
	public static void addIfNotPresent( final HeaderGroup headers, final String name, final String value ) {
		if ( ! headers.containsHeader( name ) ) headers.addHeader( new Header( name, value ) );
	}
	private BoundedInputStream stream;
}
