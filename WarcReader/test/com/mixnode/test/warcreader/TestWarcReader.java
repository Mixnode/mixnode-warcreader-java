package com.mixnode.test.warcreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import com.mixnode.warcreader.WarcReader;
import com.mixnode.warcreader.record.WarcRecord;
import com.mixnode.warcreader.record.WarcRecord.WarcType;

public class TestWarcReader {
	public static void main( final String[] arg ) throws Exception {
		InputStream in = new FileInputStream(new File("warc4.gz"));
		WarcReader warcReader = new WarcReader(in);
		WarcRecord record;
		int numRecords = 0;
		while ((record = warcReader.readRecord()) != null) {
			if ((++numRecords)%1 == 0)
				System.out.println(numRecords);
			//if (record.getWarcHeaders().containsHeader("Mixnode-Guessed-Charset")) {
			//	System.out.println(record.getWarcHeaders().getFirstHeader("Mixnode-Guessed-Charset").getValue());
			//}			
			if(record.getType() == WarcType.response) {
				HttpResponse resp = (HttpResponse)record.getWarcContentBlock();
				if (resp.containsHeader("Content-Type") == false) {
					System.out.println("No content type");
				}
				else
					System.out.println(resp.getFirstHeader("Content-Type").getValue());
			}
			else if(record.getType() == WarcType.metadata) {
				//System.out.println(Arrays.toString(record.getWarcHeaders().getAllHeaders()));
				System.out.println("body:" +IOUtils.toString(record.getWarcContentBlock().payload(), "utf-8"));
			}
		}
	}
}
