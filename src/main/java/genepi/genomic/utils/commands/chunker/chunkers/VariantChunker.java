package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class VariantChunker implements IChunker {

    private IVariantReader reader;
    private List<Chunk> chunks = new ArrayList<>();
    private int size;
    int chunkNumber = 0;
    private Class<? extends IVariantWriter> writerClass;

    @Override
    public void setReader(IVariantReader reader) {
        this.reader = reader;
    }

    @Override
    public void setWriterClass(Class<? extends IVariantWriter> clazz) {
        this.writerClass = clazz;
    }

    @Override
    public void executes() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.chunkNumber++;
        Chunk chunk = null;
        String chrom = "";
        int start = 0;
        int end = getSize();
        int numberVariants = 0, numberSamples = reader.getNumberOfAllSamples();
        List<Variant> variantList = new ArrayList<>(); //List for writer to get all data from variants in chunk

        IVariantWriter writer = null;

        if (writerClass != null){
            Constructor[] constructor = writerClass.getConstructors();
            writer = (IVariantWriter) constructor[0].newInstance(reader.getHeader());
        }

        while (reader.next()) {
            Variant v = reader.getVariant();

            //Check if chromosome is different and not empty
            if(!v.getChromosome().equals(chrom) && !chrom.isEmpty()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                chunk.setVariants(numberVariants);
                this.addChunks(chunk);
                chunkNumber++;
                numberVariants = 0;

                start = 0;
                end = getSize();
                chrom = v.getChromosome();
                if(writerClass != null) {
                    try {
                        writer.setFileOutputByChunk(chunk);
                        chunk.setFile(new File(writer.getFileOutput()));
                        for (Variant variant : variantList) {
                            writer.setVariant(variant);
                            writer.write();
                        }
                        writer.setCalls(0);
                        writer.close();
                        variantList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    chunk.setFile(reader.getFile());
                }
            }

            // Check if the current chunk has reached the maximum number of variants
            if (numberVariants >= getSize()) {
                chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                chunk.setVariants(numberVariants);
                addChunks(chunk);
                chunkNumber++;

                if(writerClass != null) {
                    try {
                        writer.setFileOutputByChunk(chunk);
                        chunk.setFile(new File(writer.getFileOutput()));
                        for (Variant variant : variantList) {
                            writer.setVariant(variant);
                            writer.write();
                        }
                        writer.setCalls(0);
                        writer.close();
                        variantList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    chunk.setFile(reader.getFile());
                }

                // Reset chunk-related information
                numberVariants = 0;
                chrom = "";
                numberSamples = reader.getNumberOfAllSamples();

                start = end + 1;
                end = start + getSize() - 1;
            }

            // Add variant to current chunk count
            numberVariants++;
            if (writerClass != null){
                variantList.add(v);
                if(variantList.size() >= 500){ //write current 500 variants already to avoid heapspace problem
                    chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
                    writer.setFileOutputByChunk(chunk);
                    chunk.setFile(new File(writer.getFileOutput()));
                    try {
                        for (Variant variant : variantList) {
                            writer.setVariant(variant);
                            writer.write();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    variantList.clear();
                }
            }
            if (chrom.isEmpty()) {
                chrom = v.getChromosome();
            }
        }

        // Create last chunk and add to list if variants exist
        if (numberVariants > 0) {
            chunk = new Chunk(chunkNumber, chrom, start, end, numberSamples);
            chunk.setVariants(numberVariants);
            addChunks(chunk);
            if(writerClass != null) {
                try {
                    writer.setFileOutputByChunk(chunk);
                    chunk.setFile(new File(writer.getFileOutput()));
                    for (Variant variant : variantList) {
                        writer.setVariant(variant);
                        writer.write();
                    }
                    writer.setCalls(0);
                    writer.close();
                    variantList.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                chunk.setFile(reader.getFile());
            }
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

    protected void addChunks(Chunk chunk) {
        this.chunks.add(chunk);
    }
}
