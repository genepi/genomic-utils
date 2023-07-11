package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.Genotype;
import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.Variant;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VcfReader implements IVariantReader{
    private String[] header;
    private Variant currentVariant;
    private VCFFileReader reader;
    private CloseableIterator<VariantContext> iterator;
    private File file;

    public VcfReader(File file) {
        this.file = file;
        reader = new VCFFileReader(file, false);
        iterator = reader.iterator();
        // read header
        header = new String[]{String.valueOf(reader.getFileHeader())};

        // open vcf file

    }

    @Override
    public Variant getVariant() {
        return this.currentVariant;
    }

    @Override
    public boolean next() {
        VariantContext vc = iterator.next();

        Object[] genotypesArray = vc.getGenotypes().toArray();
        List<Genotype> genotypeList = new ArrayList<>();

        for(Object object : genotypesArray) {
            String objectString = object.toString();
            String[] parts = objectString.substring(1, objectString.length() -1 ).split("\\s"); //Trennt den String in 2 Teile (id und genotype) und entfernt die eckigen Klammern + das Leerzeichen
            if(parts.length == 2) {
                String id = parts[0];
                String genotype = parts[1];
                Genotype genotypeObject = new Genotype(id, genotype);
                genotypeList.add(genotypeObject);
            }
        }
        currentVariant = new Variant(vc.getContig(),vc.getStart(),genotypeList);
        return iterator.hasNext();
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public int getNumberOfSamples() {
        return reader.getFileHeader().getNGenotypeSamples();
    }

    @Override
    public int getNumberOfVariants() {
        return 0; //muss noch gemacht werden falls nötig
    }

    @Override
    public void close() {
        this.reader.close();
    }
}
