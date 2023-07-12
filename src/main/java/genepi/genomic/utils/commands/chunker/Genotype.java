package genepi.genomic.utils.commands.chunker;

import java.util.Map;

public class Genotype {

    private String id;
    private String genotype;
    private Map<String, Object> extendedAttributes;
    private int depth;

    public Genotype(String id, String genotype, Map<String, Object> extendedAttributes, int depth) {
        this.id = id;
        this.genotype = genotype;
        this.extendedAttributes = extendedAttributes;
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public String getGenotype() {
        return genotype;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Genotype{" +
                "id='" + id + '\'' +
                ", genotype='" + genotype + '\'' +
                ", extendedAttributes=" + extendedAttributes +
                ", depth=" + depth +
                '}';
    }
}
