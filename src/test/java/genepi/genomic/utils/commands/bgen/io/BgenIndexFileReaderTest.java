package genepi.genomic.utils.commands.bgen.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class BgenIndexFileReaderTest {

	@Test
	public void testCorrectFile() throws IOException {
		BgenIndexFileReader reader = new BgenIndexFileReader("test-data/bgen/example.bgen.bgi");
		int variants = 0;
		while (reader.next()) {
			variants++;
		}
		reader.close();
		assertEquals(1000, variants);
	}
}
