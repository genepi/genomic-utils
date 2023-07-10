package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.Variant;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;

public class VcfReader implements IVariantReader{
    private String[] header;
    private Variant currentVariant;
    private VCFFileReader reader;
    public VcfReader(File file) {
        // read header
        // open vcf file
    }

    @Override
    public Variant getVariant() {
        return null;
    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public int getNumberOfSamples() {
        return 0;
    }

    @Override
    public int getNumberOfVariants() {
        return 0;
    }

    @Override
    public void close() {

    }
}
