package genepi.genomic.utils.commands.regenie;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import genepi.io.text.GzipLineWriter;
import genepi.io.text.LineReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class RegenieSplitCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input regenie file", required = true)
	private String input;

	@Option(names = "--dict", description = "input dict file", required = true)
	private String dict;

	@Option(names = "--output", description = "output directory", required = true)
	private String output;

	@Option(names = "--sep", description = "input separator. default: space", required = false)
	private char separator = ' ';

	@Option(names = "--output-sep", description = "output separator. default: space", required = false)
	private char outputSeparator = ' ';

	
	public void setInput(String input) {
		this.input = input;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setDict(String dict) {
		this.dict = dict;
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		if (input.isEmpty()) {
			throw new Exception("No input file provided.");
		}

		List<Phenotype> phenotypes = readDictionary(dict);

		CsvTableReader reader = new CsvTableReader(input, separator);

		List<String> fixedColumns = new Vector<String>();
		for (String column : reader.getColumns()) {
			boolean isPhenotype = false;
			for (Phenotype phenotype : phenotypes) {
				if (phenotype.isPhentoypeColumn(column)) {
					phenotype.addColumn(column);
					isPhenotype = true;
				}
			}
			if (!isPhenotype) {
				fixedColumns.add(column);
			}
		}

		System.out.println("Fixed Columns: " + fixedColumns);
		for (Phenotype phenotype : phenotypes) {
			System.out.println("Phenotype '" + phenotype.getName() + "': " + phenotype.getColumns());
		}

		List<GzipLineWriter> writers = new Vector<GzipLineWriter>();
		for (Phenotype phenotype : phenotypes) {
			String filename = output + phenotype.getFilename();
			GzipLineWriter writer = new GzipLineWriter(filename);
			writer.write(createHeader(fixedColumns, phenotype));
			writers.add(writer);
		}

		while (reader.next()) {
			for (int i = 0; i < phenotypes.size(); i++) {
				Phenotype phenotype = phenotypes.get(i);
				GzipLineWriter writer = writers.get(i);
				writer.write(createLine(reader, fixedColumns, phenotype.getColumns()));
			}
		}

		for (GzipLineWriter writer : writers) {
			writer.close();
		}

		return 0;
	}

	private String createHeader(List<String> fixedColumns, Phenotype phenotype) {
		StringBuffer buffer = new StringBuffer();
		for (String column : fixedColumns) {
			if (!buffer.isEmpty()) {
				buffer.append(outputSeparator);
			}
			buffer.append(column);
		}
		for (String column : phenotype.getColumns()) {			
			buffer.append(outputSeparator);
			String cleanColumn = phenotype.cleanColumn(column);
			buffer.append(cleanColumn);
		}
		return buffer.toString();
	}
	
	private String createLine(ITableReader reader, List<String> fixedColumns, List<String> columns) {
		StringBuffer buffer = new StringBuffer();
		for (String column : fixedColumns) {
			if (!buffer.isEmpty()) {
				buffer.append(outputSeparator);
			}
			buffer.append(reader.getString(column));
		}
		for (String column : columns) {
			buffer.append(outputSeparator);
			buffer.append(reader.getString(column));
		}
		return buffer.toString();
	}


	private List<Phenotype> readDictionary(String dict) throws IOException {
		List<Phenotype> phenotypes = new Vector<Phenotype>();
		LineReader lineReader = new LineReader(dict);
		while (lineReader.next()) {
			String line = lineReader.get();
			String[] tiles = line.split(" ");
			String column = tiles[0];
			String name = tiles[1];
			Phenotype phenotype = new Phenotype(column, name);
			phenotypes.add(phenotype);
		}
		lineReader.close();
		return phenotypes;
	}

	class Phenotype {
		private String id;

		private String name;

		private List<String> columns = new Vector<String>();

		public Phenotype(String id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getColumns() {
			return columns;
		}

		public void setColumns(List<String> columns) {
			this.columns = columns;
		}

		public void addColumn(String column) {
			this.columns.add(column);
		}

		public boolean isPhentoypeColumn(String column) {
			return column.endsWith("." + id);
		}

		public String cleanColumn(String column) {
			return column.replaceAll("." + id, "");
		}
		
		public String getFilename() {
			return name + ".regenie.gz";
		}

	}

}