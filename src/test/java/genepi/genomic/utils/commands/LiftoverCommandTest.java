package genepi.genomic.utils.commands;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class LiftoverCommandTest {

	@Test
	public void testLiftHg19ToHg38() throws Exception {

		LiftoverCommand command = new LiftoverCommand();
		command.setInput("test-data/snps.hg19.txt");
		command.setChr("chr");
		command.setPosition("pos");
		command.setAlt("alt");
		command.setRef("ref");
		command.setChainFile("files/chains/hg19ToHg38.over.chain.gz");
		command.setOutput("test.txt");
		assertEquals(0, (int) command.call());

		assertEquals(8, command.getTotal());
		assertEquals(8, command.getResolved());
		assertEquals(0, command.getFailed());
		assertEquals(6, command.getFlipped());

	}
	
	@Test
	public void testLiftHg38ToHg19() throws Exception {

		LiftoverCommand command = new LiftoverCommand();
		command.setInput("test-data/snps.hg38.txt");
		command.setChr("chr");
		command.setPosition("pos");
		command.setAlt("alt");
		command.setRef("ref");
		command.setChainFile("files/chains/hg38ToHg19.over.chain.gz");
		command.setOutput("test.txt");
		assertEquals(0, (int) command.call());

		assertEquals(8, command.getTotal());
		//TODO:  check: 6 snps on chr22 are after lifotver on chr 14? 
		assertEquals(2, command.getResolved());
		assertEquals(6, command.getFailed());
		assertEquals(0, command.getFlipped());

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
