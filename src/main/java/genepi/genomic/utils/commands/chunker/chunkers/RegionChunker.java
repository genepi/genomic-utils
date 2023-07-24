package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionChunker implements IChunker {

    private IVariantReader reader;
    private List<Chunk> chunks = new ArrayList<>();
    private int size;
    int chunkNumber = 0;

    @Override
    public void setReader(IVariantReader reader) {
        this.reader = reader;
    }

    @Override
    public void executes() {
        this.chunkNumber++;
        int start = 0;
        int end = getSize();
        Chunk chunk;
        String chrom = "";
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
            if (v.getPosition() > end ) {
                // Only add chunks with variants
                if (numberVariants > 0) {
                    chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
                    chunk.setVariants(numberVariants);
                    this.addChunks(chunk);
                    chunkNumber++;
                }

                // Get start and end from next chunk
                while (v.getPosition() > end) {
                    start = end + 1;
                    end = start + size - 1;
                }

                // Reset chunk-related information
                numberVariants = 0;
                chrom = "";
                numberSamples = reader.getNumberOfAllSamples();
                path = reader.getFile().toString();
            }

            // Check if the variant is within the current range
            if (v.getPosition() >= start && v.getPosition() <= end) {
                // Add variant to current chunk count
                numberVariants++;
                if (chrom.isEmpty()) {
                    chrom = v.getChromosome();
                }
            }
        }

        // Create last chunk and add to list if variants exist
        if (numberVariants > 0) {
            chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
            chunk.setVariants(numberVariants);
            this.addChunks(chunk);
        }
    }

    @Override
    public List<Chunk> getChunks() {
        return this.chunks;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    protected void addChunks(Chunk chunk){
        this.chunks.add(chunk);
    }
}
