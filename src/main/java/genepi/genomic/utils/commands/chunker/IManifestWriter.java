package genepi.genomic.utils.commands.chunker;

import java.util.List;

public interface IManifestWriter {
    public void setVcfChunks(List<VcfChunk> chunks);
    public void write(String filename);
}
