package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.IChunker;
import genepi.genomic.utils.commands.chunker.IManifestWriter;
import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.IVariantWriter;

import java.io.File;
import java.util.List;

public class VcfChunker implements IChunker {
    private int start, end, variants, samples;
    private File file;

    @Override
    public void setReader(IVariantReader reader) {

    }

    @Override
    public void setWriter(IVariantWriter writer) {

    }

    @Override
    public void setManifestWriter(IManifestWriter writer) {

    }

    @Override
    public void executes() {

    }

    @Override
    public List<VcfChunker> getChunks() {
        return null;
    }
}
