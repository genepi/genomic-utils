package genepi.genomic.utils.commands.csv;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.csv.writer.GzipLineWriter;
import genepi.genomic.utils.commands.csv.writer.ILineWriter;
import genepi.genomic.utils.commands.csv.writer.LineWriter;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command
public class CsvConcatCommand implements Callable<Integer> {

	@Parameters(description = "Set files")
	private List<String> inputs;

	@Option(names = "--output", description = "output bed file", required = true)
	private String output;

	@Option(names = "--separator", description = "Input separator. default: ,", required = false)
	private char separator = ',';
	
	@Option(names = { "--output-sep" }, description = "Output Separator. default: ,", required = false)
	private char outputSeparator = ',';

	@Option(names = "--gz", description = "Write output as gz file")
	private boolean gzip = false;

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}

	@Override
	public Integer call() throws Exception {

		assert (inputs != null);
		assert (output != null);

		if (inputs.isEmpty()) {
			throw new Exception("No chunks found to merge.");
		}

		CsvTableReader reader = new CsvTableReader(inputs.get(0), separator);

		String[] outputColumns = reader.getColumns();
		HashSet<String> columns = new HashSet(Arrays.asList(reader.getColumns()));
		reader.close();

		ILineWriter writer = null;
		if (gzip) {
			writer = new GzipLineWriter(output);
		} else {
			writer = new LineWriter(output);
		}

		writer.write(createHeader(outputColumns));

		for (String input : inputs) {
			reader = new CsvTableReader(input, separator);
			HashSet<String> _columns = new HashSet(Arrays.asList(reader.getColumns()));
			if (!columns.equals(_columns)) {
				System.out.println("Error: Different columns:\n" + inputs.get(0) + ": " + columns + "\n" + input + ": "
						+ _columns);
				return 1;
			}

			while (reader.next()) {
				writer.write(createLine(reader, outputColumns));
			}
			reader.close();
		}

		writer.close();

		return 0;
	}

	private String createHeader(String[] columns) {
		StringBuffer buffer = new StringBuffer();
		for (String column : columns) {
			if (buffer.length()!=0) {
				buffer.append(outputSeparator);
			}
			buffer.append(column);
		}
		return buffer.toString();
	}

	private String createLine(ITableReader reader, String[] columns) {
		StringBuffer buffer = new StringBuffer();
		for (String column : columns) {
			if (buffer.length()!=0) {
				buffer.append(outputSeparator);
			}
			buffer.append(reader.getString(column));
		}
		return buffer.toString();
	}

}