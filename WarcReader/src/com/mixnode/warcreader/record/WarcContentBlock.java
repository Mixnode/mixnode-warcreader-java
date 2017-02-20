package com.mixnode.warcreader.record;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * WarcContentBlock interface represents content block of a WARC record
 * Here is a list of known implementation of this interface
 * <ul>
 * <li> RequestContentBlock
 * <li> ResponseContentBlock
 * <li> DefaultContentBlock
 * </ul>
 * @author Hadi Jooybar
 */
public interface WarcContentBlock {
	/**
	 * Returns an InputStream of WARC payload
	 * Payload referres to, or contained by a WARC record as a meaningful subset of the content block
	 * @return payload InputStream
	 * @throws IOException
	 */
	InputStream payload() throws IOException;
	/**
	 * Dump content of a WARC payload to a file
	 * @param file output File
	 * @throws IOException
	 */
	void dump(File file) throws IOException;
}
