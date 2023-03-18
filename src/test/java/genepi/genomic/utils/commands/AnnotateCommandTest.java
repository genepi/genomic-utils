package genepi.genomic.utils.commands;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import genepi.genomic.utils.io.AnnotationMatchingStrategy;

public class AnnotateCommandTest {

	@Test
	public void testRegenieGz() throws Exception {

		AnnotateCommand command = new AnnotateCommand();
		command.setInput("test-data/Y2.regenie.gz");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setAnno("test-data/rsids.tsv.gz");
		command.setAnnoColumns("RSID,REF,ALT");
		command.setStrategy(AnnotationMatchingStrategy.CHROM_POS_ALLELES);
		command.setRef("ALLELE0");
		command.setAlt("ALLELE1");

		command.setOutput(createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(1000, command.getTotalLines());
		assertEquals(1000, command.getAnnotatedLines());
	}

	@Test
	public void testRegenieTxt() throws Exception {

		AnnotateCommand command = new AnnotateCommand();
		command.setInput("test-data/Y2.regenie.txt");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setAnno("test-data/rsids.tsv.gz");
		command.setAnnoColumns("RSID,REF,ALT");
		command.setStrategy(AnnotationMatchingStrategy.CHROM_POS_ALLELES);
		command.setRef("ALLELE0");
		command.setAlt("ALLELE1");

		command.setOutput(createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(1000, command.getTotalLines());
		assertEquals(1000, command.getAnnotatedLines());
	}

	@Test
	public void testTestDataWithNoAlleleSwitches() throws Exception {

		AnnotateCommand command = new AnnotateCommand();
		command.setInput("test-data/Y2.regenie.txt");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setAnno("test-data/rsids.tsv.gz");
		command.setAnnoColumns("RSID,REF,ALT");
		command.setStrategy(AnnotationMatchingStrategy.CHROM_POS_ALLELES_EXACT);
		command.setRef("ALLELE0");
		command.setAlt("ALLELE1");

		command.setOutput(createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(1000, command.getTotalLines());
		assertEquals(995, command.getAnnotatedLines());
	}

	@Test
	public void testTestDataWithoutAlleles() throws Exception {

		AnnotateCommand command = new AnnotateCommand();
		command.setInput("test-data/Y2.regenie.txt");
		command.setChr("CHROM");
		command.setPosition("GENPOS");
		command.setAnno("test-data/rsids.tsv.gz");
		command.setAnnoColumns("RSID,REF,ALT");
		command.setStrategy(AnnotationMatchingStrategy.CHROM_POS);
		command.setRef("ALLELE0");
		command.setAlt("ALLELE1");

		command.setOutput(createOutputFile());
		assertEquals(0, (int) command.call());
		assertEquals(1000, command.getTotalLines());
		// one snp hast multiple entries in index (multiallelic)
		assertEquals(999, command.getAnnotatedLines());
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
