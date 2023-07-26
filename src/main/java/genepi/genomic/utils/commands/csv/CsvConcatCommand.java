package genepi.genomic.utils.commands.csv;

import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.concurrent.Callable;

import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command
public class CsvConcatCommand implements Callable<Integer> {

	@Parameters(description = "Set files")
	private List<String> inputs;

	@Option(names = "--output", description = "output bed file", required = true)
	private String output;

	@Option(names = "--separator", description = "separator. default: ,", required = false)
	private char separator = ',';

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public Integer call() throws Exception {

		assert (inputs != null);
		assert (output != null);

		if (inputs.isEmpty()) {
			throw new Exception("No chunks found to merge.");
		}

		CsvTableReader reader = new CsvTableReader(inputs.get(0), separator);
		HashSet<String> columns = new HashSet(Arrays.asList(reader.getColumns()));
		reader.close();

		CsvTableWriter writer = new CsvTableWriter(output, separator);
		writer.setColumns(reader.getColumns());

		for (String input : inputs) {
			reader = new CsvTableReader(input, separator);
			HashSet<String> _columns = new HashSet(Arrays.asList(reader.getColumns()));
			if (!columns.equals(_columns)) {
				System.out.println("Error: Different columns:\n" + inputs.get(0) + ": " + columns + "\n" + input + ": "
						+ _columns);
				return 1;
			}

			while (reader.next()) {
				for (String column : columns) {
					writer.setString(column, reader.getString(column));
				}
				writer.next();
			}
			reader.close();
		}

		writer.close();

		return 0;
	}

}