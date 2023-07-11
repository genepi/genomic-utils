package genepi.genomic.utils.commands.chunker;

public class Genotype {

    private String id;
    private String genotype;

    public Genotype(String id, String genotype) {
        this.id = id;
        this.genotype = genotype;
    }

    public String getId() {
        return id;
    }

    public String getGenotype() {
        return genotype;
    }

    @Override
    public String toString() {
        return "[" + id + " " + genotype + "]";
    }
}
