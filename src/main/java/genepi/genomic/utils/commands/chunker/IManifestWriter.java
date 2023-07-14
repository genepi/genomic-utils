package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.chunkers.VcfChunk;

import java.util.List;

public interface IManifestWriter {
    public void setVcfChunks(List<VcfChunk> chunks);
    public void write();
    int getLinesWritten();
    void setLinesWritten(int linesWritten);
}
