package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.Genotype;
import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.Variant;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VcfReader implements IVariantReader{
    private VCFHeader header;
    private Variant currentVariant;
    private VCFFileReader reader;
    private CloseableIterator<VariantContext> iterator;
    private File file;
    private int numberVariants;
    private int numberCurrentSamples;
    private int numberCurrentVariants;

    public VcfReader(File file) {
        this.file = file;
        reader = new VCFFileReader(file, false);
        iterator = reader.iterator();
        // read header
        header = reader.getFileHeader();

        // open vcf file

    }

    @Override
    public Variant getVariant() {
        return this.currentVariant;
    }

    @Override
    public boolean next() {

        if (iterator.hasNext()) {

            VariantContext vc = iterator.next();

            GenotypesContext genotypeContext = vc.getGenotypes();
            List<Genotype> genotypeList = new ArrayList<>();
            List<String> format = new ArrayList<>();

            int i = 0;

            while (i < genotypeContext.size()) {
                Genotype genotype = new Genotype( genotypeContext.get(i).getSampleName(), genotypeContext.get(i).getExtendedAttributes(), genotypeContext.get(i).getDP());
                genotypeList.add(genotype);
                format.addAll(genotypeContext.get(i).getExtendedAttributes().keySet());
                i++;

            }

            List<String> alt = new ArrayList<>();
            for (Allele allele : vc.getAlternateAlleles()) {
                alt.add(allele.getBaseString());
            }

            currentVariant = new Variant(vc.getContig(), vc.getStart(), vc.getID(), vc.getReference().toString(), alt, String.valueOf(vc.getPhredScaledQual()), String.valueOf(vc.getFilters()), vc.getAttributes(), format, genotypeList);
            System.out.println(currentVariant);
            this.numberVariants++;
            this.numberCurrentSamples++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public File getFile() {
        return this.file;
    }

    public int getNumberOfCurrentSamples(){
        return this.numberCurrentSamples;
    }

    @Override
    public int getNumberOfAllSamples() {
        return reader.getFileHeader().getNGenotypeSamples();
    }

    @Override
    public int getNumberOfAllVariants() {
        return this.numberVariants;
    }

    public int getNumberOfCurrentVariants() {
        return this.numberCurrentVariants;
    }

    @Override
    public void close() {
        this.reader.close();
    }
}
