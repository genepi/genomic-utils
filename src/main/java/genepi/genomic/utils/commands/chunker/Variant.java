package genepi.genomic.utils.commands.chunker;

import java.util.List;
import java.util.Map;

public class Variant {
    private String chromosome;
    private long position;
    private String id;
    private String ref;
    private List<String> alt;
    private String qual;
    private String filter;
    private Map<String, Object> info;
    private List<String> format;
    private List<Genotype> genotypes;

    public Variant(String chromosome, long position, String id, String ref, List<String> alt, String qual, String filter, Map<String, Object> info, List<String> format, List<Genotype> genotypes) {
        this.chromosome = chromosome;
        this.position = position;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.info = info;
        this.format = format;
        this.genotypes = genotypes;
    }

    public String getChromosome() {
        return chromosome;
    }

    public long getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public List<String> getAlt() {
        return alt;
    }

    public String getQual() {
        return qual;
    }

    public String getFilter() {
        return filter;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public List<String> getFormat() {
        return format;
    }

    public List<Genotype> getGenotypes() {
        return genotypes;
    }

    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + position +
                ", id='" + id + '\'' +
                ", ref='" + ref + '\'' +
                ", alt=" + alt +
                ", qual='" + qual + '\'' +
                ", filter='" + filter + '\'' +
                ", info=" + info +
                ", format=" + format +
                ", genotypes=" + genotypes +
                '}';
    }
}
