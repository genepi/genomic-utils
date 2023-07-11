package genepi.genomic.utils.commands.chunker;

import java.util.Map;

public class Genotype {

    private String id;
    private Map<String, Object> attributes;

    public Genotype(String id, Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "Genotype{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
