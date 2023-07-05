package genepi.genomic.utils.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import genepi.io.text.LineReader;
import genepi.io.text.LineWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "csv-to-bed", version = App.VERSION)
public class CsvToBedCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input csv file", required = true)
	private String input;

	@Option(names = "--output", description = "output fbed ile", required = true)
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

	class GenomicRegion {

		private String chromosome;

		private int start;

		private int end;

		public String getChromosome() {
			return chromosome;
		}

		public void setChromosome(String chromosome) {
			this.chromosome = chromosome;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public GenomicRegion parse(String string) throws IOException {

			if (string.startsWith("rs")) {
				// TODO: convert rsId to position. needs build.
				throw new IOException("RsIDs no yet supported.");
			}

			// bed
			if (string.contains("\t")) {
				String[] tiles = string.split("\t");
				if (tiles.length == 3) {
					GenomicRegion location = new GenomicRegion();
					location.chromosome = tiles[0];
					int start = Integer.parseInt(tiles[1]);
					location.start = start;
					int end = Integer.parseInt(tiles[2]);
					location.end = end;
					return location;
				}
			} else if (string.contains(":")) {
				String[] tiles = string.split(":");
				if (tiles.length == 2) {
					if (string.contains("-")) {
						// region
						String[] tiles2 = tiles[1].split("-");
						if (tiles2.length == 2) {
							GenomicRegion location = new GenomicRegion();
							location.chromosome = tiles[0];
							int start = Integer.parseInt(tiles2[0]);
							location.start = start;
							int end = Integer.parseInt(tiles2[1]);
							location.end = end;
							return location;
						}
					} else {
						// single position
						GenomicRegion location = new GenomicRegion();
						location.chromosome = tiles[0];
						int start = Integer.parseInt(tiles[1]);
						location.start = start;
						location.end = start;
						return location;
					}
				}

			}

			// check if chr
			if (string.startsWith("chr")) {
				GenomicRegion location = new GenomicRegion();
				location.chromosome = string;
				location.start = 1;
				location.end = Integer.MAX_VALUE;
				return location;
			}

			// TODO: check if gene

			throw new IOException("Unknown format.");
		}

		public String toBedFormat() {
			return chromosome + "\t" + (start - 1) + "\t" + (end);
		}

	}

}