package genepi.genomic.utils.commands.chunker.chunkers;

import java.io.File;

public class VcfChunk {
    private int chunkNumber;
    private String chrom;
    private int start;
    private int end;
    private int variants;
    private int samples;
    private File file;

    public VcfChunk(int chunkNumber, String chrom, int start, int end, int samples, File file) {
        this.chunkNumber = chunkNumber;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.samples = samples;
        this.file = file;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public String getChrom() {
        return chrom;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getVariants() {
        return variants;
    }

    public void setVariants(int variants) {
        this.variants = variants;
    }

    public int getSamples() {
        return samples;
    }

    public File getFile() {
        return file;
    }
}
