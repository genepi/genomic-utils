package genepi.genomic.utils.commands.annotate;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class AnnotateGenesCommandTest {

	@Test
	public void testRegenieGz() throws Exception {

		AnnotateGenesCommand command = new AnnotateGenesCommand();
		command.setInput("test-data/annotation/example.regenie");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setSeparator(' ');
		command.setWindow(1000);
		command.setAnno("test-data/annotation/genes.hg19.csv");
		command.setAnnoColumns("GENE_CHROMOSOME,GENE_START,GENE_END,GENE_NAME,GENE_DISTANCE");
		command.setAnnoChr("GENE_CHROMOSOME");
		command.setAnnoStart("GENE_START");
		command.setAnnoEnd("GENE_END");

		command.setOutput("annotated.txt.gz");//createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(1000, command.getTotalLines());
		assertEquals(1000, command.getAnnotatedLines());
	}
	
	
	/*@Test
	public void testRegenieGz2() throws Exception {

		AnnotateGenesCommand command = new AnnotateGenesCommand();
		command.setInput("test-data/lpa_man.regenie.filtered.gz");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setSeparator('\t');
		command.setWindow(1000);
		command.setAnno("test-data/annotation/genes.hg19.csv");
		//command.setAnno("test-data/annotation/nearest_gene.GRCh37.sorted.bed");
		command.setAnnoColumns("GENE_CHROMOSOME,GENE_START,GENE_END,GENE_NAME");
		command.setAnnoChr("GENE_CHROMOSOME");
		command.setAnnoStart("GENE_START");
		command.setAnnoEnd("GENE_END");

		String output = "annotated.txt";
		command.setOutput(output);//createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(21967, command.getTotalLines());
		assertEquals(21967, command.getAnnotatedLines());
		int ok = 0;
		int wrong = 0;
		CsvTableReader reader = new CsvTableReader(output, '\t');
		while(reader.next()) {
			if (!reader.getString("GENE_NAME2").equals(reader.getString("GENE_NAME"))) {
				System.out.println(reader.getString("rsid") + " " + reader.getString("GENPOS") + ": " + reader.getString("GENE_NAME") + " ("+ reader.getString("GENE_START")+ "-"+reader.getString("GENE_END") +")"+ " vs. " + reader.getString("GENE_NAME2")+ " ("+ reader.getString("GENE_START2")+ "-"+reader.getString("GENE_END2") +")");
				wrong++;
			} else {
				ok++;
			}
		}
		
		System.out.println("Wrong: " + wrong / (double)(wrong + ok) * 100.0 + "%");
		
		reader.close();
	}*/

	

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
