package com.github.lukfor.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.lukfor.App;
import com.github.lukfor.io.AnnotationFileReader;

import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "merge", version = App.VERSION)
public class MergeCsvCommand {

	@Option(names = { "--input" }, description = "Input filename", required = true)
	private String input;

	@Option(names = { "--chr" }, description = "Chromosome column in input file", required = true)
	private String chr;

	@Option(names = { "--position" }, description = "Position column in input file", required = true)
	private String position;

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private String output;

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
			"--anno-ref" }, description = "Ref allele column in annotation fil", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoRef = "REF";

	@Option(names = {
			"--anno-alt" }, description = "Alt allele column in annotation fil", required = false, showDefaultValue = Visibility.ALWAYS)
	private String annoAlt = "ALT";

	@Option(names = {
			"--check-alleles" }, description = "Check for alleles", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean checkAlleles = false;

	@Option(names = {
			"--allow-allele-switches" }, description = "Allow allele switches when check for alleles is enabled", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean allowAlleleSwitch = false;

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

	public void setAllowAlleleSwitch(boolean allowAlleleSwitch) {
		this.allowAlleleSwitch = allowAlleleSwitch;
	}

	public void setCheckAlleles(boolean checkAlleles) {
		this.checkAlleles = checkAlleles;
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

		if (checkAlleles) {
			annotationReader = new AnnotationFileReader(anno, annotationColumns, annoSparator, annoRef, annoAlt);
		} else {
			annotationReader = new AnnotationFileReader(anno, annotationColumns, annoSparator);
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

		// TODO: ref and alt allele? next step?

		CsvTableWriter writer = new CsvTableWriter(output, separator);
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

			if (checkAlleles) {
				String refValue = reader.getString(ref);
				String altValue = reader.getString(alt);
				annotations = annotationReader.query(chrValue, positionValue, refValue, altValue, allowAlleleSwitch);
			} else {
				annotations = annotationReader.query(chrValue, positionValue);
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
		System.out.println("Error: " + message);
	}

	protected void warning(String message) {
		if (!suppressWarnings) {
			System.out.println("Warning: " + message);
		}
	}

	protected void info(String message) {
		System.out.println(message);
	}

	protected void success(String message) {
		System.out.println(message);
	}

}
