package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class RegionChunker implements IChunker {

    private IVariantReader reader;
    private List<Chunk> chunks = new ArrayList<>();
    private int size;
    int chunkNumber = 0;
    private Class<? extends IVariantWriter> writerClass;
    private int numberVariants = 0;
    private IVariantWriter writer = null;
    private Chunk chunk = null;
//    private List<Variant> variantList;

    @Override
    public void setReader(IVariantReader reader) {
        this.reader = reader;
    }

    @Override
    public void setWriterClass(Class<? extends IVariantWriter> clazz) {
        this.writerClass = clazz;
    }

    @Override
    public void executes() throws Exception {
        this.chunkNumber++;
        int start = 0;
        int end = getSize();
        String chrom = "";
        int numberSamples = reader.getNumberOfAllSamples();

        if (writerClass != null) {
            Constructor[] constructor = writerClass.getConstructors();
            writer = (IVariantWriter) constructor[0].newInstance(reader.getHeader());
        }

        while (reader.next()) {
            Variant v = reader.getVariant();

            //Check if chromosome is different and not empty
            if (!v.getChromosome().equals(chrom) && !chrom.isEmpty()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                this.addChunkToList(chunk);

                start = 0;
                end = getSize();
                chrom = v.getChromosome();

                this.writerClassCheck();
                this.chunk = null;
            }
            if (v.getPosition() > end) {
                // Only add chunks with variants
                if (numberVariants > 0) {
                    chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                    this.addChunkToList(chunk);
                    this.writerClassCheck();
                    this.chunk = null;
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
            }

            // Check if the variant is within the current range
            if (v.getPosition() >= start && v.getPosition() <= end) {
                // Add variant to current chunk count
                numberVariants++;
                if (writerClass != null) {

                    if (chunk == null) { //check if chunk already exists --> no need to create new chunk every variant
                        chrom = v.getChromosome();
                        chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                    }
                    writer.setFileOutputByChunk(chunk);
                    chunk.setFile(new File(writer.getFileOutput()));
                    writer.setVariant(v);
                    writer.write();
                }
                if (chrom.isEmpty()) {
                    chrom = v.getChromosome();
                }
            }
        }
        // Create last chunk and add to list if variants exist
        if (numberVariants > 0) {
            chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
            this.addChunkToList(chunk);
            this.writerClassCheck();
        }
    }

    private void writerClassCheck() throws Exception {
        if (writerClass != null) {
            try {
                writer.setFileOutputByChunk(chunk);
                chunk.setFile(new File(writer.getFileOutput()));
                writer.setCalls(0);
                writer.close();
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else {
            chunk.setFile(reader.getFile());
        }
    }

    private void addChunkToList(Chunk chunk) {
        chunk.setVariants(numberVariants);
        this.addChunks(chunk);
        chunkNumber++;
        numberVariants = 0;
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

    protected void addChunks(Chunk chunk) {
        this.chunks.add(chunk);
    }
}