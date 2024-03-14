package genepi.genomic.utils.commands.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import genepi.io.FileUtil;

public class CsvToBedCommandTest {

	@Test
	public void testWithGeneName() throws Exception {
		String output = createOutputFile();
		CsvToBedCommand command = new CsvToBedCommand();
		command.setInput("test-data/csv/lpa.txt");
		command.setGenes("test-data/csv/genes.GRCh37.sorted.bed");
		command.setOutput(output);
		assertEquals(0, (int) command.call());
		assertTrue(FileUtil.readFileAsString(output).contains("6	160952514	161087407"));
	}

	@Test
	public void testWithGeneNameAndMissingIndex() throws Exception {
		String output = createOutputFile();
		CsvToBedCommand command = new CsvToBedCommand();
		command.setInput("test-data/csv/lpa.txt");
		command.setOutput(output);
		assertEquals(1, (int) command.call());
	}

	// rs 4808801 19 18571141

	@Test
	public void testWithRsId() throws Exception {
		String output = createOutputFile();
		CsvToBedCommand command = new CsvToBedCommand();
		command.setInput("test-data/csv/rs.txt");
		command.setDbSnp("test-data/csv/dbsnp-index.small.txt.gz");
		command.setOutput(output);
		assertEquals(0, (int) command.call());
		System.out.println(FileUtil.readFileAsString(output));
		assertTrue(FileUtil.readFileAsString(output).contains("19	18571140	18571141"));
	}

	@Test
	public void testWithRsIdAndMissingIndex() throws Exception {
		String output = createOutputFile();
		CsvToBedCommand command = new CsvToBedCommand();
		command.setInput("test-data/csv/rs.txt");
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
