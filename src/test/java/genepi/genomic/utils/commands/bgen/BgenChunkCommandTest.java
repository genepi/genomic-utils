package genepi.genomic.utils.commands.bgen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import genepi.genomic.utils.commands.bgen.util.ChunkingStrategy;
import genepi.io.FileUtil;

public class BgenChunkCommandTest {

	@Test
	public void testBgenWithRange() throws Exception {
		String output = createOutputFile();
		BgenChunkCommand command = new BgenChunkCommand();
		command.setInputs(Arrays.asList("test-data/bgen/example.bgen"));
		command.setOutput(output);
		command.setChunkSize(100);
		assertEquals(0, (int) command.call());
		assertEquals(FileUtil.readFileAsString(output), FileUtil.readFileAsString("test-data/bgen/expected.txt"));
	}
	
	@Test
	public void testBgenWithVariants() throws Exception {
		String output = createOutputFile();
		BgenChunkCommand command = new BgenChunkCommand();
		command.setInputs(Arrays.asList("test-data/bgen/example.bgen"));
		command.setStrategy(ChunkingStrategy.VARIANTS);
		command.setOutput(output);
		command.setChunkSize(100);
		assertEquals(0, (int) command.call());
		assertEquals(FileUtil.readFileAsString(output), FileUtil.readFileAsString("test-data/bgen/expected.txt"));
	}

	@Test
	public void testVcfWithVariants() throws Exception {
		String output = createOutputFile();
		BgenChunkCommand command = new BgenChunkCommand();
		command.setInputs(Arrays.asList("test-data/chr20.R50.merged.1.330k.recode.small.vcf.gz"));
		command.setStrategy(ChunkingStrategy.VARIANTS);
		command.setOutput(output);
		command.setChunkSize(100);
		assertEquals(0, (int) command.call());
		System.out.println(FileUtil.readFileAsString(output));
		//assertEquals(FileUtil.readFileAsString(output), FileUtil.readFileAsString("test-data/Y2.regenie.gz"));
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
