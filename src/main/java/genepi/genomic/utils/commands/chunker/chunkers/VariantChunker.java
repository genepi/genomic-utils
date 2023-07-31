package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class VariantChunker implements IChunker {

    private IVariantReader reader;
    private List<Chunk> chunks = new ArrayList<>();
    private int size;
    int chunkNumber = 0;
    private Class<? extends IVariantWriter> writerClass;
    private int numberVariants = 0;
    private IVariantWriter writer = null;
    private Chunk chunk = null;

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
        String chrom = "";
        int start = 0;
        int end = getSize();
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
            }

            // Check if the current chunk has reached the maximum number of variants
            if (numberVariants >= getSize()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                this.addChunkToList(chunk);
                this.writerClassCheck();

                // Reset chunk-related information
                numberVariants = 0;
                chrom = "";
                numberSamples = reader.getNumberOfAllSamples();

                start = end + 1;
                end = start + getSize() - 1;
            }
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
        return this.size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    protected void addChunks(Chunk chunk) {
        this.chunks.add(chunk);
    }
}
