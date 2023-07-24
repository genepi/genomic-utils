package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void executes();
    public List<Chunk> getChunks();
    public void setSize(int size);
}
