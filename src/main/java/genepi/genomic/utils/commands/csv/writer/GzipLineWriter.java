package genepi.genomic.utils.commands.csv.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

public class GzipLineWriter implements ILineWriter {

	private BufferedWriter bw;

	private boolean first = true;

	public GzipLineWriter(String filename) throws IOException {
		bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(new File(filename)))));
		first = true;
	}

	public void write(String line) throws IOException {
		if (first) {
			first = false;
		} else {
			bw.newLine();
		}

		bw.write(line);
	}

	public void close() throws IOException {
		bw.close();
	}

}
