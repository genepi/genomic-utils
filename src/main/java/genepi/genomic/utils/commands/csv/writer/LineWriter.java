package genepi.genomic.utils.commands.csv.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LineWriter implements ILineWriter {

	private BufferedWriter bw;

	private boolean first = true;

	private boolean data = false;

	public LineWriter(String filename) throws IOException {
		bw = new BufferedWriter(new FileWriter(new File(filename), false));
		first = true;
		data = false;
	}

	public void write(String line) throws IOException {
		write(line, true);
	}

	public void write(String line, boolean dataRow) throws IOException {
		if (first) {
			first = false;
		} else {
			bw.newLine();
		}

		this.data = dataRow;

		bw.write(line);
	}

	public void close() throws IOException {
		bw.close();
	}

	public boolean hasData() {
		return data;
	}

}
