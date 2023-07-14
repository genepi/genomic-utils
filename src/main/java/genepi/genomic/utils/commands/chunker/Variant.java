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
        this.setChromosome(chromosome);
        this.setPosition(position);
        this.setId(id);
        this.setRef(ref);
        this.setAlt(alt);
        this.setQual(qual);
        this.setFilter(filter);
        this.setInfo(info);
        this.setFormat(format);
        this.setGenotypes(genotypes);
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

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setAlt(List<String> alt) {
        this.alt = alt;
    }

    public void setQual(String qual) {
        this.qual = qual;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

    public void setGenotypes(List<Genotype> genotypes) {
        this.genotypes = genotypes;
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
