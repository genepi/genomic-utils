package genepi.genomic.utils.commands.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.Test;

import genepi.io.FileUtil;

public class CsvConcatCommandTest {

	@Test
	public void testWithCorrectFiles() throws Exception {
		String output = createOutputFile();
		CsvConcatCommand command = new CsvConcatCommand();
		command.setInputs(Arrays.asList("test-data/csv/fileA.txt", "test-data/csv/fileB.txt"));
		command.setOutput(output);
		assertEquals(0, (int) command.call());

		assertEquals(FileUtil.readFileAsString(output), FileUtil.readFileAsString("test-data/csv/expected.txt"));
	}

	@Test
	public void testWithCorrectGzFiles() throws Exception {
		String output = createOutputFile();
		CsvConcatCommand command = new CsvConcatCommand();
		command.setInputs(Arrays.asList("test-data/csv/fileA.txt", "test-data/csv/fileB.txt"));
		command.setOutput(output);
		command.setGzip(true);
		assertEquals(0, (int) command.call());

		assertEquals(FileUtil.readFileAsString(new GzipCompressorInputStream(new FileInputStream(output))),
				FileUtil.readFileAsString("test-data/csv/expected.txt"));
	}

	@Test
	public void testWithDifferentColumns() throws Exception {
		String output = createOutputFile();
		CsvConcatCommand command = new CsvConcatCommand();
		command.setInputs(Arrays.asList("test-data/csv/fileA.txt", "test-data/csv/fileC.txt"));
		command.setOutput(output);
		assertEquals(1, (int) command.call());
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
