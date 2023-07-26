package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void setWriterClass(Class<? extends IVariantWriter> clazz); // null --> no chunks written
    public void executes() throws InstantiationException, IllegalAccessException;
    public List<Chunk> getChunks();
    public void setSize(int size);
}
