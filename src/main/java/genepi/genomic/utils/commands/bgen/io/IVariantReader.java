package genepi.genomic.utils.commands.bgen.io;

import java.io.IOException;

public interface IVariantReader {

	public Variant get();

	public boolean next() throws IOException;

	public void close() throws IOException;

}