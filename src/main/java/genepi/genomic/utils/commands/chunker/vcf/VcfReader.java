package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.Genotype;
import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.Variant;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.TribbleException;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VcfReader implements IVariantReader {
    private VCFHeader header;
    private Variant currentVariant;
    private VCFFileReader reader;
    private CloseableIterator<VariantContext> iterator;
    private File file;
    private int numberVariants;
    private int numberCurrentVariants;

    public VcfReader(File file) throws IOException {
        try {
            this.file = file;
            reader = new VCFFileReader(file, false);
            iterator = reader.iterator();
            this.header = reader.getFileHeader();
        } catch (TribbleException e){
            throw new IOException(e.getMessage());
        }

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

            for (htsjdk.variant.variantcontext.Genotype g : vc.getGenotypes()) {
                Genotype genotype = new Genotype(g.getSampleName(), g.getGenotypeString(), g.getExtendedAttributes(), g.getDP());
                genotypeList.add(genotype);
                format.addAll(genotypeContext.get(i).getExtendedAttributes().keySet());
                i++;

            }

            List<String> alt = new ArrayList<>();
            for (Allele allele : vc.getAlternateAlleles()) {
                alt.add(allele.getBaseString());
            }

            currentVariant = new Variant(vc.getContig(), vc.getStart(), vc.getID(), vc.getReference().getBaseString(), alt, String.valueOf(vc.getPhredScaledQual()), String.valueOf(vc.getFilters()), vc.getAttributes(), format, genotypeList);
            this.numberVariants++;
            this.numberCurrentVariants++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public File getFile() {
        return this.file;
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
    public VCFHeader getHeader(){
        return this.header;
    }

    @Override
    public void close() {
        this.reader.close();
    }
}
