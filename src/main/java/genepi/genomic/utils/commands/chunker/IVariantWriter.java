package genepi.genomic.utils.commands.chunker;

import java.io.IOException;
import java.util.List;

public interface IVariantWriter {
    public void setVariants(List<Variant> variants);
    public void write() throws IOException;
    public void close();
}
