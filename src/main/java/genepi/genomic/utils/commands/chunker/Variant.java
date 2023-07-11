package genepi.genomic.utils.commands.chunker;

import java.util.List;
import java.util.Map;

public class Variant {
    private String chromosome;
    private long position;
    private List<Genotype> genotypes;
    private String id;
    private String ref;
    private List<String> alt;
    private String qual;
    private Map<String, Object> info;
    private List<String> format;

    public Variant(String chromosome, long position, List<Genotype> genotypes, String id, String ref, List<String> alt, String qual, Map<String, Object> info, List<String> format) {
        this.chromosome = chromosome;
        this.position = position;
        this.genotypes = genotypes;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.info = info;
        this.format = format;
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
