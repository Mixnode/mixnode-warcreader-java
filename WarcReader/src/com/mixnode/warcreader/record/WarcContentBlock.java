package com.mixnode.warcreader.record;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface WarcContentBlock {
	InputStream payload() throws IOException;
	void dump(File file) throws IOException;
}
