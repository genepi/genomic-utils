package genepi.genomic.utils.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import genepi.io.table.reader.CsvTableReader;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;

public class AnnotationFileReader {

	private TabixReader reader;

	private Map<String, Integer> columnsIndex = new HashMap<String, Integer>();

	private char separator;

	private boolean checkAlleles = false;

	private int indexRef;

	private int indexAlt;

	public AnnotationFileReader(String input, String[] columns, char separator, boolean comments) throws IOException {

		this.separator = separator;

		CsvTableReader tableReader = new CsvTableReader(input, separator, !comments);
		
		System.err.println("Detected columns: " + Arrays.toString(tableReader.getColumns()));

		for (String column : columns) {
			if (!tableReader.hasColumn(column)) {
				throw new IOException("Column '" + column + "' not found in file '" + input + "'");
			}
			int index = tableReader.getColumnIndex(column);
			columnsIndex.put(column, index);
		}

		tableReader.close();

		reader = new TabixReader(input);

	}

	public AnnotationFileReader(String input, String[] columns, char separator, boolean comments, String ref, String alt)
			throws IOException {

		this.separator = separator;

		CsvTableReader tableReader = new CsvTableReader(input, separator, !comments);

		System.err.println("Detected columns: " + Arrays.toString(tableReader.getColumns()));
		
		checkAlleles = true;

		if (!tableReader.hasColumn(ref)) {
			throw new IOException("Column '" + ref + "' not found in file '" + input + "'");
		}
		indexRef = tableReader.getColumnIndex(ref);

		if (!tableReader.hasColumn(alt)) {
			throw new IOException("Column '" + alt + "' not found in file '" + input + "'");
		}
		indexAlt = tableReader.getColumnIndex(alt);

		for (String column : columns) {
			if (!tableReader.hasColumn(column)) {
				throw new IOException("Column '" + column + "' not found in file '" + input + "'");
			}
			int index = tableReader.getColumnIndex(column);
			columnsIndex.put(column, index);
		}

		tableReader.close();

		reader = new TabixReader(input);

	}

	public List<Map<String, String>> query(String chr, int position) throws IOException {

		return query(chr, position, null, null, false);
	}

	public List<Map<String, String>> query(String chr, int position, String ref, String alt, boolean allowAlleleSwitch)
			throws IOException {

		List<Map<String, String>> annotations = new Vector<>();

		Iterator result = reader.query(chr, position - 1, position);
		String line = result.next();
		while (line != null) {

			String[] tiles = line.split("" + separator);

			boolean match = true;
			if (checkAlleles) {
				
				String annoRef = tiles[indexRef].trim();
				String annoAlt = tiles[indexAlt].trim();
				
				if (allowAlleleSwitch) {
					match = (annoRef.equalsIgnoreCase(ref) && containsAllele(annoAlt, alt))
							|| (annoRef.equalsIgnoreCase(alt) && containsAllele(annoAlt, ref));
				} else {
					match = (annoRef.equalsIgnoreCase(ref) && containsAllele(annoAlt, alt));
				}

			}

			if (match) {
				Map<String, String> values = new HashMap<>();
				for (String column : columnsIndex.keySet()) {
					int index = columnsIndex.get(column);
					values.put(column, tiles[index]);
				}
				annotations.add(values);
			}

			line = result.next();
		}

		return annotations;
	}

	public void close() {
		reader.close();
	}
	
	protected boolean containsAllele(String multi, String allele) {
		
		String[] alleles = multi.split(",");
		for (String a: alleles) {
			if (a.equalsIgnoreCase(allele)) {
				return true;
			}
		}
		
		return false;
	}

}
