package genepi.genomic.utils.commands.csv;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.annotate.io.GzipCsvTableWriter;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ITableWriter;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Visibility;

public class CsvFilterCommand implements Callable<Integer> {

	@Option(names = "--input", description = "Input file", required = true)
	private String input;

	@Option(names = "--output", description = "Output file ", required = true)
	private String output;

	@Option(names = "--filter-column", description = "Column name to apply filter", required = true)
	private String filterColumn = "";

	@Option(names = "--ignore-values", description = "Values to ignore from the filter column", required = false)
	private String ignoreValues = "";

	@Option(names = "--limit", description = "Specifiy upper limit to filter", required = true)
	private double limit;

	@Option(names = "--separator", description = "Separator of input file. default: ,", required = false, showDefaultValue = Visibility.ALWAYS)
	private char separator = ',';

	@Option(names = "--output-sep", description = "Separator of output file. default: ,", required = false, showDefaultValue = Visibility.ALWAYS)
	private char outputSeparator = ',';

	@Option(names = { "--gz" }, description = "Write file as gzip", required = false)
	private boolean outputGzip = false;

	@Option(names = { "--output-quote" }, description = "Quote entries in output file", required = false)
	private boolean outputQuote = false;

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setGzip(boolean outputGzip) {
		this.outputGzip = outputGzip;
	}

	public void setSeparator(char sep) {
		this.separator = sep;
	}

	public void setOutputSeparator(char outputSep) {
		this.outputSeparator = outputSep;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setFilterColumn(String filterColumn) {
		this.filterColumn = filterColumn;
	}

	public void setIgnoreValues(String ignoreValues) {
		this.ignoreValues = ignoreValues;
	}

	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		ITableWriter writer = null;
		if (outputGzip) {
			writer = new GzipCsvTableWriter(output, outputSeparator, outputQuote);
		} else {
			writer = new CsvTableWriter(output, outputSeparator, outputQuote);
		}

		List<String> ignoreList = Arrays.asList(ignoreValues.split(","));

		CsvTableReader reader = new CsvTableReader(input, separator);

		writer.setColumns(reader.getColumns());

		if (!reader.hasColumn(filterColumn)) {
			System.err.println("Column " + filterColumn + "not found.");
			return 1;
		}

		int line = 0;
		while (reader.next()) {
			line++;
			String value = reader.getString(filterColumn);
			try {

				if (ignoreList.contains(value) || Double.valueOf(value) < limit) {
					continue;
				}
				writer.setRow(reader.getRow());
				writer.next();
			} catch (NumberFormatException e) {
				System.out.println("Ignoring line " + line + ". Value '" + value + "' cannot be parsed.");
			}
		}

		reader.close();
		writer.close();
		return 0;
	}

	public static void main(String... args) {
		int exitCode = new CommandLine(new CsvFilterCommand()).execute(args);
		System.exit(exitCode);
	}

}
