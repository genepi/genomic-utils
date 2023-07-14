package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;

import java.util.List;

public interface IChunker {

    public void setReader(IVariantReader reader);
    public void setWriter(IVariantWriter writer); // null --> no chunks written
    public void setManifestWriter(IManifestWriter writer); // null --> no manifest
    public void executes(int region);
    public List<RegionChunker> getChunks();
}
