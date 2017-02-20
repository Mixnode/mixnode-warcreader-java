package com.mixnode.warcreader.record;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.AbstractHttpMessage;

import com.mixnode.warcreader.WarcFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;

public class DefaultContentBlock implements WarcContentBlock {

	protected InputStream payload;
	public DefaultContentBlock(final InputStream input) {
		payload = input;
	}


	@Override
	public String toString() {
		return "";
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
}
