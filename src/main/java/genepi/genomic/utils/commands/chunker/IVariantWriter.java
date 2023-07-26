package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;

import java.io.IOException;

public interface IVariantWriter {

    public void setVariant(Variant variant);
    public void write() throws IOException;
    public void close();
    public void setFileOutputByChunk(Chunk chunk);
    public String getFileOutput();
    public void setCalls(int calls);
    public void setFileOutputDefault(String outputDefault);
}
