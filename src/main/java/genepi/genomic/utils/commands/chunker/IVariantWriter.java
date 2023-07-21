package genepi.genomic.utils.commands.chunker;

import java.io.IOException;

public interface IVariantWriter {
    public void setVariant(Variant variant);
    public void setNumberofSamples(int numberOfSamples);
    public void write() throws IOException;
    public void close();
}
