package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.Chunk;
import genepi.io.text.LineWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManifestWriter implements IManifestWriter {

    private String header, filename;
    int linesWritten = 0;
    List<Chunk> chunks = new ArrayList<>();
    LineWriter lineWriter;

    public ManifestWriter(String filename) {
        this.filename = filename;
        this.setHeader();
    }

    @Override
    public void setVcfChunks(List<Chunk> chunks) {
        this.chunks = chunks;
    }

    @Override
    public void write(){
            try {
                lineWriter = new LineWriter(this.filename);
                lineWriter.write(getHeader());

                for (Chunk chunk : chunks) {
                    String line = chunk.getChunkNumber() + "\t" + chunk.getChrom() + "\t" + chunk.getStart() + "\t" + chunk.getEnd() +
                            "\t" + chunk.getFile() + "\t" + chunk.getVariants() + "\t" + chunk.getSamples();
                    lineWriter.write(line);
                }
                lineWriter.close();
            } catch (IOException e) {
                System.out.println("An error occured:" + e.getMessage());
            }
    }

    public void setHeader() {
        this.header = "CHUNKNUMBER \t CHROM \t START \t END \t PATH \t NUMBERVARIANTS \t NUMBERSAMPLES";
    }

    public String getHeader() {
        return header;
    }
}
