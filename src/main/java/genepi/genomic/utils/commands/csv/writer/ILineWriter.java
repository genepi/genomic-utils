package genepi.genomic.utils.commands.csv.writer;

import java.io.IOException;

public interface ILineWriter {

	public void write(String line) throws IOException;

	public void close() throws IOException;

}
