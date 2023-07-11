package genepi.genomic.utils.commands.chunker;

import java.util.List;

public class Variant {
    private String chromosome;
    private long position;
    private List<Genotype> genotypes;

    public Variant(String chromosome, long position, List<Genotype> genotypes){
        this.chromosome = chromosome;
        this.position = position;
        this.genotypes = genotypes;
    }

    public String getChromosome() {
        return chromosome;
    }

    public long getPosition() {
        return position;
    }

    public List<Genotype> getGenotypes() {
        return genotypes;
    }

    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + position +
                ", genotypes=" + genotypes +
                '}';
    }
}
