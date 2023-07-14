package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void setWriter(IVariantWriter writer); // null --> no chunks written
    public void setmWriter(IManifestWriter writer); // null --> no manifest
    public void executes();
    public List<VcfChunker> getChunks();
}
