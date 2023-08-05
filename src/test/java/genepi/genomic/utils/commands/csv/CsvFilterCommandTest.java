package genepi.genomic.utils.commands.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.Test;

import genepi.io.FileUtil;

public class CsvFilterCommandTest {

	@Test
	public void testGzFilteringWithNAValues() throws Exception {
		String output = createOutputFile();
		CsvFilterCommand command = new CsvFilterCommand();
		command.setInput("test-data/regenie/regenie_step2_example_Y1.regenie.gz");
		command.setOutput(output);
		command.setOutputSeparator('\t');
		command.setSeparator('\t');
		command.setLimit(2);
		command.setGzip(true);
		command.setFilterColumn("LOG10P");
		command.setIgnoreValues("NA");
		assertEquals(0, (int) command.call());
		
		assertEquals(FileUtil.readFileAsString(new GzipCompressorInputStream(new FileInputStream(output))),
				FileUtil.readFileAsString(new GzipCompressorInputStream(new FileInputStream("test-data/regenie/regenie_filtered.txt.gz"))));

	}
	
	protected String createOutputFile() {
		try {
			File tempFile = File.createTempFile("output", ".txt");
			tempFile.deleteOnExit();
			return tempFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
}
