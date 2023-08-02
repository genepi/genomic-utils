package genepi.genomic.utils.commands.annotate.io;

import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import au.com.bytecode.opencsv.CSVWriter;
import genepi.io.table.writer.AbstractTableWriter;

public class GzipCsvTableWriter extends AbstractTableWriter {

	private CSVWriter writer;
	public String[] currentLine;
	private Map<String, Integer> columns2Index = new HashMap<String, Integer>();

	public GzipCsvTableWriter(String filename) {
		try {
			Writer fileWriter = new OutputStreamWriter(new GzipCompressorOutputStream(new FileOutputStream(filename)));
			writer = new CSVWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GzipCsvTableWriter(String filename, char separator) {
		try {
			Writer fileWriter = new OutputStreamWriter(new GzipCompressorOutputStream(new FileOutputStream(filename)));
			writer = new CSVWriter(fileWriter, separator);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GzipCsvTableWriter(String filename, char separator, boolean quote) {
		try {
			Writer fileWriter = new OutputStreamWriter(new GzipCompressorOutputStream(new FileOutputStream(filename)));
			writer = new CSVWriter(fileWriter, separator,
					quote ? CSVWriter.DEFAULT_QUOTE_CHARACTER
							: CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getColumnIndex(String column) {
		return columns2Index.get(column);
	}

	@Override
	public boolean next() {
		writer.writeNext(currentLine);
		for (int i = 0; i < currentLine.length; i++) {
			currentLine[i] = "";
		}
		return true;
	}

	@Override
	public void setColumns(String[] columns) {
		currentLine = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columns2Index.put(columns[i], i);
			currentLine[i] = "";
		}
		writer.writeNext(columns);
	}

	@Override
	public void setDouble(int column, double value) {
		currentLine[column] = value + "";
	}

	@Override
	public void setInteger(int column, int value) {
		currentLine[column] = value + "";
	}

	@Override
	public void setString(int column, String value) {
		currentLine[column] = value;
	}

	@Override
	public void setRow(String[] row) {
		currentLine = row;
	}

}
