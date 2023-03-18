package genepi.genomic.utils.commands;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import genepi.genomic.utils.io.AnnotationFileReader;
import genepi.genomic.utils.io.AnnotationMatchingStrategy;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "annotate", version = App.VERSION)
public class AnnotateCommand implements Callable<Integer> {

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
			"--ref" }, description = "Ref allele column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String ref = "REF";

	@Option(names = {
			"--alt" }, description = "Alt allele column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String alt = "ALT";

	@Option(names = {
			"--anno-ref" }, description = "Ref allele column in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoRef = "REF";

	@Option(names = {
			"--anno-alt" }, description = "Alt allele column in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoAlt = "ALT";

	@Option(names = {
			"--anno-comments" }, description = "Activate this flag to ignore lines starting with # in annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean annoComments = false;

	@Option(names = {
			"--strategy" }, description = "Defines the matching strategy between lines in input file and annotation file", required = false, showDefaultValue = Visibility.ALWAYS)
	private AnnotationMatchingStrategy strategy = AnnotationMatchingStrategy.CHROM_POS;

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private String output;

	@Option(names = { "--output-sep" }, description = "Separator of output file", required = false)
	private char outputSep = '\t';

	@Option(names = { "--output-quote" }, description = "Quote entries in output file", required = false)
	private boolean outputQuote = false;

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

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setAnnoAlt(String annoAlt) {
		this.annoAlt = annoAlt;
	}

	public void setAnnoRef(String annoRef) {
		this.annoRef = annoRef;
	}

	public void setStrategy(AnnotationMatchingStrategy strategy) {
		this.strategy = strategy;
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

		AnnotationFileReader annotationReader = null;

		switch (strategy) {
		case CHROM_POS:
			annotationReader = new AnnotationFileReader(anno, annotationColumns, annoSparator, annoComments);
			break;
		case CHROM_POS_ALLELES:
		case CHROM_POS_ALLELES_EXACT:
			annotationReader = new AnnotationFileReader(anno, annotationColumns, annoSparator, annoComments, annoRef,
					annoAlt);
			break;
		}

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

		CsvTableWriter writer = new CsvTableWriter(output, outputSep, outputQuote);
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

			List<Map<String, String>> annotations = null;

			switch (strategy) {
			case CHROM_POS:
				annotations = annotationReader.query(chrValue, positionValue);
				break;
			case CHROM_POS_ALLELES:
			case CHROM_POS_ALLELES_EXACT:
				String refValue = reader.getString(ref).trim();
				String altValue = reader.getString(alt).trim();
				boolean allowAlleleSwitches = (strategy == AnnotationMatchingStrategy.CHROM_POS_ALLELES);
				annotations = annotationReader.query(chrValue, positionValue, refValue, altValue, allowAlleleSwitches);
				break;
			}

			if (annotations.size() == 1) {

				for (String annotationColumn : annotations.get(0).keySet()) {
					writer.setString(annotationColumn, annotations.get(0).get(annotationColumn));
				}

				annotatedLines++;

			} else if (annotations.size() > 1) {
				warning("multiple entries found for '" + chrValue + ":" + positionValue + "': " + annotations);
			} else {
				warning("no annotation entry found for '" + chrValue + ":" + positionValue + "'");
			}

			writer.next();
		}
		reader.close();
		writer.close();

		success("Annotated " + annotatedLines + " of " + totalLines + " lines.");
		success("Written annotated lines to '" + output + "'.");

		return 0;
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
