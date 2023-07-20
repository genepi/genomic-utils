package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VariantChunker implements IChunker {

    private IVariantReader reader;
    private IVariantWriter writer;
    private IManifestWriter manifestWriter;
    private List<Chunk> chunks = new ArrayList<>();
    private int size;
    int chunkNumber = 0;

    @Override
    public void setReader(IVariantReader reader) {
        this.reader = reader;
    }

    @Override
    public void setWriter(IVariantWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setManifestWriter(IManifestWriter writer) {
        this.manifestWriter = writer;
    }

    @Override
    public IManifestWriter getManifestWriter() {
        return this.manifestWriter;
    }

    @Override
    public void executes() {
        this.chunkNumber++;
        Chunk chunk;
        String chrom = "";
        int start = 0;
        int end = getSize();
        int numberVariants = 0, numberSamples = reader.getNumberOfAllSamples();
        String path = reader.getFile().toString();

        while (reader.next()) {
            Variant v = reader.getVariant();

            //Check if chromosome is different and not empty
            if(!v.getChromosome().equals(chrom) && !chrom.isEmpty()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
                chunk.setVariants(numberVariants);
                this.addChunks(chunk);
                chunkNumber++;
                numberVariants = 0;

                start = 0;
                end = getSize();
                chrom = v.getChromosome();
            }

            // Check if the current chunk has reached the maximum number of variants
            if (numberVariants >= getSize()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
                chunk.setVariants(numberVariants);
                addChunks(chunk);
                chunkNumber++;

                // Reset chunk-related information
                numberVariants = 0;
                chrom = "";
                numberSamples = reader.getNumberOfAllSamples();
                path = reader.getFile().toString();

                start = end + 1;
                end = start + getSize() - 1;
            }

            // Add variant to current chunk count
            numberVariants++;
            if (chrom.isEmpty()) {
                chrom = v.getChromosome();
            }
        }

        // Create last chunk and add to list if variants exist
        if (numberVariants > 0) {
            chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
            chunk.setVariants(numberVariants);
            addChunks(chunk);
        }
    }

    @Override
    public List<Chunk> getChunks() {
        return this.chunks;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getNumberChunks() {
        return chunks.size();
    }

    @Override
    public void addChunks(Chunk chunk) {
        this.chunks.add(chunk);
    }
}
