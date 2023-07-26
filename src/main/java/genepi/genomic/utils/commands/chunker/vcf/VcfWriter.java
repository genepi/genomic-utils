package genepi.genomic.utils.commands.chunker.vcf;

import genepi.genomic.utils.commands.chunker.IVariantWriter;
import genepi.genomic.utils.commands.chunker.Variant;
import genepi.genomic.utils.commands.chunker.chunkers.Chunk;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class VcfWriter implements IVariantWriter {

    private VCFHeader header;
    private String output;
    private Variant variant;
    private VariantContextWriterBuilder builder;
    private VariantContextBuilder vcBuilder;
    private VariantContextWriter vcfWriter;
    private int calls = 0;
    private static String OUTPUTDIR = "";

    public VcfWriter(VCFHeader header) {
        this.header = header;

        builder = new VariantContextWriterBuilder();
        vcBuilder = new VariantContextBuilder();
    }

    @Override
    public void write() throws IOException {

        if(this.calls == 0) {
            vcfWriter = builder.setOutputFile(this.output).unsetOption(Options.INDEX_ON_THE_FLY).build();
            vcfWriter.writeHeader(this.header);
        }

        this.calls++;

        List<Genotype> genotypes = new ArrayList<>();


        VariantContextBuilder vcBuilder = new VariantContextBuilder()
                .start(variant.getPosition())
                .stop(variant.getPosition())
                .chr(variant.getChromosome())
                .id(variant.getId());

        if (variant.getFilter().equals("")) {
            vcBuilder.passFilters();
        } else {
            vcBuilder.filter(variant.getFilter());
        }

        List<Allele> alleles = new ArrayList<>();

        Allele refAllele = Allele.create(variant.getRef(), true);
        alleles.add(refAllele);
        for (String a : variant.getAlt()) {
            alleles.add(Allele.create(a, false));
        }

        List<Allele> genotypeAlleles = new ArrayList<>();

        int i = 0;
        while (i < variant.getGenotypes().size()) {
            genepi.genomic.utils.commands.chunker.Genotype genotypeObject = variant.getGenotypes().get(i);
            String genotype = genotypeObject.getGenotype();
            String genotypePart1 = genotype.split("[\\|\\/]")[0];
            String genotypePart2 = genotype.split("[\\|\\/]")[1];
            Allele genotypeAllele1;
            Allele genotypeAllele2;
            // cases 0|0 or 1|1 or 2|2
            if (genotypePart1.equals(genotypePart2)) {
                boolean isRef = false;
                if (genotypePart1.equals(variant.getRef())) {
                    isRef = true;
                } else if (genotypePart2.equals(variant.getRef())) {
                    isRef = true;
                } else {
                    isRef = false;
                }

                genotypeAllele1 = Allele.create(genotypePart1, isRef);
                genotypeAllele2 = Allele.create(genotypePart2, isRef);
                // cases 1|0 or 0|1 or 1/2
            } else {
                if (genotypePart1.equals(variant.getRef())) {
                    genotypeAllele1 = Allele.create(genotypePart1, true);
                    genotypeAllele2 = Allele.create(genotypePart2, false);
                } else if (genotypePart2.equals(variant.getRef())) {
                    genotypeAllele1 = Allele.create(genotypePart1, false);
                    genotypeAllele2 = Allele.create(genotypePart2, true);
                } else {
                    genotypeAllele1 = Allele.create(genotypePart1, false);
                    genotypeAllele2 = Allele.create(genotypePart2, false);
                }
            }
            genotypeAlleles.add(genotypeAllele1);
            genotypeAlleles.add(genotypeAllele2);

            List<Allele> genotypeAllelesCopy = new ArrayList<>(genotypeAlleles); //create copy of genotypeAlleles otherwise the clearing of the list will affect each genotype

            GenotypeBuilder gb = new GenotypeBuilder(genotypeObject.getId(), genotypeAllelesCopy);
            gb.attributes(genotypeObject.getExtendedAttributes());
            gb.DP(genotypeObject.getDepth());
            genotypes.add(gb.make());
            genotypeAlleles.clear();
            i++;
        }
        vcBuilder.alleles(alleles);
        vcBuilder.genotypes(genotypes);
        vcfWriter.add(vcBuilder.make());
        alleles.clear();
        genotypes.clear();
    }

    @Override
    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    @Override
    public void setFileOutputByChunk(Chunk chunk) {
        if (OUTPUTDIR.equals("")) {
            this.output = "CHUNK" + chunk.getChunkNumber() + "FROM" + chunk.getStart() + "-" + chunk.getEnd() + ".vcf";
        } else {
            this.output = OUTPUTDIR + "/CHUNK" + chunk.getChunkNumber() + "FROM" + chunk.getStart() + "-" + chunk.getEnd() + ".vcf";
        }
    }
    @Override
    public void setFileOutputDefault(String outputDefault){
        this.output = outputDefault;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public static void setOutputDir(String outputDir) {
        OUTPUTDIR = outputDir;
    }

    @Override
    public String getFileOutput() {
        return this.output;
    }

    public int getCalls() {
        return calls;
    }

    @Override
    public void setCalls(int calls) {
        this.calls = calls;
    }

    @Override
    public void close() {
        this.vcfWriter.close();
    }
}
