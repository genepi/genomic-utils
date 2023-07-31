package genepi.genomic.utils.commands.regenie;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RegenieSplitCommandTest {

	@Test
	public void testSplit() throws Exception {
		RegenieSplitCommand command = new RegenieSplitCommand();
		command.setInput("test-data/regenie/regenie.txt");
		command.setDict("test-data/regenie/regenie.dict");
		command.setOutput("regenie");
		assertEquals(0,(int)command.call());
	}
	
}
