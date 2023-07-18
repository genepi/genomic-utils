package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionChunker implements IChunker {

    private IVariantReader reader;
    private IVariantWriter writer;
    private IManifestWriter manifestWriter;
    private List<Chunk> chunks;
    private int size;

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
    public void executes() {
        int start = 0;
        int end = getSize();
        int chunkNumber = 0;
        Chunk chunk;
        chunks = new ArrayList<>();
        String chrom = "";
        int numberVariants = 0, numberSamples = reader.getNumberOfAllSamples();
        String path = reader.getFile().toString();

        while (reader.next()) {
            Variant v = reader.getVariant();

            if (v.getPosition() > end) {
                // Only add chunks with variants
                if (numberVariants > 0) {
                    chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
                    chunk.setVariants(numberVariants);
                    this.addChunks(chunk);
                    chunkNumber++;
                }

                // Check if the variant belongs to the next chunk
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

    @Override
    public int getNumberChunks(){
        return this.chunks.size();
    }

    @Override
    public void addChunks(Chunk chunk){
        this.chunks.add(chunk);
    }
}
