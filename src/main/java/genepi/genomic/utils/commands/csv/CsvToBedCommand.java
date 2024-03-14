package genepi.genomic.utils.commands.csv;

import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.csv.region.DbSnpIndex;
import genepi.genomic.utils.commands.csv.region.GeneIndex;
import genepi.genomic.utils.commands.csv.region.GenomicRegion;
import genepi.io.text.LineReader;
import genepi.io.text.LineWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class CsvToBedCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input csv file", required = true)
	private String input;

	@Option(names = "--output", description = "output bed file", required = true)
	private String output;

	@Option(names = "--build", description = "genome build", required = true)
	private String build = "hg19";

	@Option(names = "--genes", description = "gene index file", required = false)
	private String genes = null;

	@Option(names = "--dbsnp", description = "dbsnp index file", required = false)
	private String dbsnp = null;

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setGenes(String genes) {
		this.genes = genes;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setDbSnp(String dbsnp) {
		this.dbsnp = dbsnp;
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		if (genes != null) {
			GeneIndex.load(genes, build);
		}

		if (dbsnp != null) {
			DbSnpIndex.setFilename(dbsnp);
		}

		LineWriter writer = new LineWriter(output);
		LineReader reader = new LineReader(input);

		while (reader.next()) {
			String line = reader.get();
			try {
				GenomicRegion region = GenomicRegion.parse(line.trim(), build);
				writer.write(region.toBedFormat());
			} catch (Exception e) {
				System.out.println("Error processing query: '" + line + "': " + e.getMessage());
				return 1;
			}
		}

		reader.close();
		writer.close();

		return 0;
	}

}