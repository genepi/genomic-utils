package genepi.genomic.utils.commands.chunker;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void setWriter(IVariantWriter writer); // null --> no chunks written
    public void setManifestWriter(IManifestWriter writer); // null --> no manifest
    public void executes();
    public List<VcfChunk> getChunks();
}
