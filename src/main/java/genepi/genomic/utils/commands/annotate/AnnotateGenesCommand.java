package genepi.genomic.utils.commands.annotate;

import java.util.List;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.annotate.io.GeneAnnotationFileReader;
import genepi.genomic.utils.commands.annotate.io.GeneAnnotationFileReader.Gene;
import genepi.genomic.utils.commands.annotate.io.GzipCsvTableWriter;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ITableWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command
public class AnnotateGenesCommand implements Callable<Integer> {

	@Option(names = { "--input" }, description = "Input filename", required = true)
	private String input;

	@Option(names = { "--chr" }, description = "Chromosome column in input file", required = true)
	private String chr;

	@Option(names = { "--position", "--pos" }, description = "Position column in input file", required = true)
	private String position;

	@Option(names = {
			"--sep" }, description = "Separator of input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private char separator = '\t';

	@Option(names = { "--anno" }, description = "Annotation filename", required = true)
	private String anno;

	@Option(names = { "--anno-columns" }, description = "Annotation columns", required = true)
	private String annoColumns;

	@Option(names = {
			"--anno-sep" }, description = "Separator of annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private char annoSparator = '\t';

	@Option(names = {
			"--anno-chr" }, description = "Chr column in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoChr = "chr";

	@Option(names = {
			"--anno-start" }, description = "start position column in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoStart = "start";

	@Option(names = {
			"--anno-end" }, description = "end position column in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoEnd = "end";

	@Option(names = {
			"--anno-comments" }, description = "Activate this flag to ignore lines starting with # in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean annoComments = false;

	@Option(names = {
			"--window" }, description = "window / max distance", required = false, showDefaultValue = Visibility.ALWAYS)
	private int window = 0;

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private String output;

	@Option(names = { "--output-sep" }, description = "Separator of output file", required = false)
	private char outputSep = '\t';

	@Option(names = { "--output-quote" }, description = "Quote entries in output file", required = false)
	private boolean outputQuote = false;

	@Option(names = { "--output-gzip" }, description = "Write file as gzip", required = false)
	private boolean outputGzip = false;

	@Option(names = {
			"--suppress-warnings" }, description = "Suppress warnings", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean suppressWarnings = false;

	private int totalLines = 0;

	private int annotatedLines = 0;

	public void setInput(String input) {
		this.input = input;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setWindow(int window) {
		this.window = window;
	}

	public void setAnnoColumns(String columns) {
		this.annoColumns = columns;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setOutputSep(char outputSep) {
		this.outputSep = outputSep;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public void setAnnoChr(String annoChr) {
		this.annoChr = annoChr;
	}

	public void setAnnoStart(String annoStart) {
		this.annoStart = annoStart;
	}

	public void setAnnoEnd(String annoEnd) {
		this.annoEnd = annoEnd;
	}

	public void setAnnoSparator(char annoSparator) {
		this.annoSparator = annoSparator;
	}

	public int getTotalLines() {
		return totalLines;
	}

	public int getAnnotatedLines() {
		return annotatedLines;
	}

	public Integer call() throws Exception {

		assert (anno != null);
		assert (annoColumns != null);
		assert (input != null);
		assert (chr != null);
		assert (position != null);
		assert (output != null);

		String[] annotationColumns = annoColumns.split(",");

		info("Load annotation file from '" + anno + "'.");

		GeneAnnotationFileReader annotationReader = new GeneAnnotationFileReader(anno, annotationColumns, annoSparator,
				annoComments, annoChr, annoStart, annoEnd);

		info("Load input file from '" + input + "'.");

		CsvTableReader reader = new CsvTableReader(input, separator);

		if (!reader.hasColumn(chr)) {
			error("Column '" + chr + "' not found in file '" + input + "'");
			return 1;
		}

		if (!reader.hasColumn(position)) {
			error("Column '" + position + "' not found in file '" + input + "'");
			return 1;
		}

		ITableWriter writer = null;
		if (outputGzip) {
			writer = new GzipCsvTableWriter(output, outputSep, outputQuote);
		} else {
			writer = new CsvTableWriter(output, outputSep, outputQuote);
		}

		String[] inputColumns = reader.getColumns();
		String[] outputColumns = concat(annotationColumns, inputColumns);

		writer.setColumns(outputColumns);

		info("Annotating input file....");

		while (reader.next()) {

			totalLines++;

			for (String inputColumn : inputColumns) {
				writer.setString(inputColumn, reader.getString(inputColumn));
			}

			String chrValue = reader.getString(chr);
			int positionValue = reader.getInteger(position);

			List<Gene> annotations = annotationReader.query(chrValue, positionValue, window);

			Gene annotation = null;

			if (annotations.size() == 1) {
				annotation = annotations.get(0);
				annotatedLines++;
			} else if (annotations.size() >= 1) {
				annotation = mergeAnnotations(annotations);
				annotatedLines++;
			} else {
				warning("no annotation entry found for '" + chrValue + ":" + positionValue + "'");
			}
			if (annotation != null) {
				for (String column : annotationColumns) {
					writer.setString(column, annotation.get(column));
				}
			}
			writer.next();
		}
		reader.close();
		writer.close();

		success("Annotated " + annotatedLines + " of " + totalLines + " lines.");
		success("Written annotated lines to '" + output + "'.");

		return 0;
	}

	private Gene mergeAnnotations(List<Gene> annotations) {
		Gene merged = new Gene();
		for (Gene gene : annotations) {
			for (String column : gene.keySet()) {
				String value = gene.get(column);
				if (merged.get(column) == null) {
					merged.put(column, value);
				} else {
					String existing = merged.get(column);
					merged.put(column, existing + "," + value);
				}
			}
		}
		return merged;
	}

	private String[] concat(String[] array1, String[] array2) {
		String[] outputColumns = new String[array2.length + array1.length];
		for (int i = 0; i < array2.length; i++) {
			outputColumns[i] = array2[i];
		}
		for (int i = 0; i < array1.length; i++) {
			outputColumns[array2.length + i] = array1[i];
		}
		return outputColumns;
	}

	protected void error(String message) {
		System.err.println("Error: " + message);
	}

	protected void warning(String message) {
		if (!suppressWarnings) {
			System.err.println("Warning: " + message);
		}
	}

	protected void info(String message) {
		System.err.println(message);
	}

	protected void success(String message) {
		System.err.println(message);
	}

}
