package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ManifestWriter implements IManifestWriter {

    private String header;
    int calls = 0;
    int linesWritten = 0;

    public ManifestWriter() {
        this.setHeader();
    }

    @Override
    public void setVcfChunks(List<VcfChunker> chunks) {

    }

    @Override
    public void write(String filename, int chromosome, int start, int end, String path, int variants, int samples, int chunkNumber){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename, true));
            if(calls == 0) {
                writer.println(this.getHeader());
                this.linesWritten++;
            }
            writer.println(chunkNumber + "\t" + chromosome + "\t" + start + "\t" + end + "\t" + path + "\t" + variants + "\t" + samples);
            writer.close();
            calls++;
            this.linesWritten++;
        } catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    public void setHeader() {
        this.header = "CHUNKNUMBER \t CHROM \t START \t END \t PATH \t NUMBERVARIANTS \t NUMBERSAMPLES";
    }

    public int getLinesWritten() {
        return linesWritten;
    }

    public String getHeader() {
        return header;
    }
}
