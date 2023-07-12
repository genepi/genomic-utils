package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.IVariantWriter;
import genepi.genomic.utils.commands.chunker.Variant;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;


public class VcfWriter implements IVariantWriter {

    private VCFHeader header;

    VariantContextWriterBuilder builder = new VariantContextWriterBuilder();
    VariantContextBuilder vcBuilder = new VariantContextBuilder();

    public VcfWriter(VCFHeader header){
        this.header = header;
    }

    @Override
    public void setVariant(Variant variant) {

    }

    @Override
    public void setNumberofSamples(int numberOfSamples) {

    }

    @Override
    public void write() {
        VariantContextWriter vcfWriter = builder.build();
        vcfWriter.writeHeader(this.header);
    }

    @Override
    public void close() {

    }
}
