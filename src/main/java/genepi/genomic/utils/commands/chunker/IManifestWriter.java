package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;

import java.util.List;

public interface IManifestWriter {
    public void setVcfChunks(List<Chunk> chunks);
    public void write();
}
