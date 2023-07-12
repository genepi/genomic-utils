package genepi.genomic.utils.commands.chunker;

import java.util.Map;

public class Genotype {

    private String id;
    private Map<String, Object> extendedAttributes;
    private int depth;

    public Genotype(String id, Map<String, Object> attributes, int depth) {
        this.id = id;
        this.extendedAttributes = attributes;
        this.depth = depth;
    }

    public String getId() {
        return id;
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
                ", attributes=" + extendedAttributes +
                ", depth=" + depth +
                '}';
    }
}
