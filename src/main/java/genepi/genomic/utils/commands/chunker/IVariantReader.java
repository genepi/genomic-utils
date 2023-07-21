package genepi.genomic.utils.commands.chunker;

import java.io.File;

public interface IVariantReader {
    public Variant getVariant(); //return current variant
    public boolean next(); //load next variant, store current variant. if eof --> false.
    public File getFile();
    public int getNumberOfAllSamples();
    public int getNumberOfAllVariants();
    public Object getHeader();
    public void close();
}
