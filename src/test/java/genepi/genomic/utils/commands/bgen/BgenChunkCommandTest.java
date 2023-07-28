package genepi.genomic.utils.commands.bgen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import genepi.io.FileUtil;

public class BgenChunkCommandTest {

	@Test
	public void testWithCorrectFiles() throws Exception {
		String output = createOutputFile();
		BgenChunkCommand command = new BgenChunkCommand();
		command.setInputs(Arrays.asList("test-data/bgen/example.bgen"));
		command.setOutput(output);
		command.setChunkSize(100);
		assertEquals(0, (int) command.call());
		assertEquals(FileUtil.readFileAsString(output), FileUtil.readFileAsString("test-data/bgen/expected.txt"));
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
