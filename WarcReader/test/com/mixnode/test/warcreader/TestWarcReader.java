package com.mixnode.test.warcreader;

import com.mixnode.warcreader.WarcReader;
import com.mixnode.warcreader.record.WarcRecord;
import com.mixnode.warcreader.record.WarcRecord.WarcType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;

public class TestWarcReader {
	public static void main( final String[] arg ) throws Exception {
		String fileName = "warc.gz";
		if (arg[0] != null)
			fileName = arg[0];
		InputStream in = null;
		try {
		in = new FileInputStream(new File(fileName));
		}
		catch(IOException e) {
			System.out.println("warc.gz is not found. Either rename your warc to warc.gz or enter the name as first main argument");
		}
		WarcReader warcReader = new WarcReader(in);
		WarcRecord record;
		int numRecords = 0;
		while ((record = warcReader.readRecord()) != null) {
			if ((++numRecords)%1000 == 0)
				System.out.println(numRecords);
			if(record.getType() == WarcType.response) {
				HttpResponse resp = (HttpResponse)record.getWarcContentBlock();
				if (resp.containsHeader("Content-Type") == false) {
					System.out.println("No content type");
				}
				else
					System.out.println(resp.getFirstHeader("Content-Type").getValue());
			}
		}
	}
}
