package genepi.genomic.utils.commands.csv;

import java.util.concurrent.Callable;

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

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		LineReader reader = new LineReader(input);
		LineWriter writer = new LineWriter(output);

		while (reader.next()) {
			String line = reader.get();
			GenomicRegion region = new GenomicRegion().parse(line);
			writer.write(region.toBedFormat());
		}

		reader.close();
		writer.close();

		return 0;
	}

}