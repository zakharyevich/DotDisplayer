package org.vzdot;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

public class DotDisplayerTest {

	@Test
	public void testPrint() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/testfiles");
		File dir = new File(url.toURI().getRawPath());
		for (File f :dir.listFiles()){
			if (f.isFile()) {
			System.out.println("Display a dependency list from "+f.getAbsolutePath());	
			(new DotDisplayer()).print(f.getAbsolutePath());
			System.out.println();
			}
		}
		
		
	}

}
