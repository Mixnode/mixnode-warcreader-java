package com.mixnode.warcreader.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * A simple implementation of a WarcContentBlock for 
 * Most of WARC-Types. This class simply output the input stream
 * into the payloadStream or a file
 * @author Hadi Jooybar
 */
public class DefaultContentBlock implements WarcContentBlock {

	protected InputStream payload;

	/**
	 * DefaultContentBlock constructor
	 * @param input Input stream for content block.
	 */
	public DefaultContentBlock(final InputStream input) {
		payload = input;
	}


	@Override
	public String toString() {
		return payload.toString();
	}


	/**
	 * Return content block stream as payload
	 * @return payload stream
	 */
	@Override
	public InputStream payload() throws IOException {
		return payload;
	}

	/**
	 * Dump content of a WARC payload to a file
	 * @param file output File
	 * @throws IOException
	 */
	@Override
	public void dump(File file) throws IOException {
		FileOutputStream out = new FileOutputStream (file);
		IOUtils.copy(payload(), out);
		out.close();
		
	}
}
