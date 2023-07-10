package genepi.genomic.utils.commands.chunker;

public interface IVariantWriter {
    public void setVariant(Variant variant);
    public void setNumberofSamples(int numberOfSamples);
    public void write();
    public void close();
}
