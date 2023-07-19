package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void setWriter(IVariantWriter writer); // null --> no chunks written
    public void setManifestWriter(IManifestWriter writer); // null --> no manifest
    public IManifestWriter getManifestWriter();
    public void executes();
    public List<Chunk> getChunks();
    public void setSize(int size);
    public int getNumberChunks();
    public void addChunks(Chunk chunk);
}
