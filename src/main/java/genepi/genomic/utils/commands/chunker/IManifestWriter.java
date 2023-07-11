package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;

import java.util.List;

public interface IManifestWriter {
    public void setVcfChunks(List<VcfChunker> chunks);
    public void write(String filename);
}
