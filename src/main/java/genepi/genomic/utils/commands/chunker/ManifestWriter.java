package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ManifestWriter implements IManifestWriter {

    private String header;
    private int chromosome, start, end, variants, samples;
    private List<Variant> variantList;

    public ManifestWriter(List<Variant> variants) {
        this.variantList = variants;
        this.setHeader();
    }

    @Override
    public void setVcfChunks(List<VcfChunker> chunks) {

    }

    @Override
    public void write(String filename){
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.println(this.getHeader());
            for (Variant v : variantList) {
                writer.println(v.getChromosome() + '\t' + this.start + '\t' + this.end + '\t' + this.variants + '\t' + this.samples);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    public void setHeader() {
        this.header = "CHROM \t START \t END \t VARIANTS \t SAMPLES";
    }

    public String getHeader() {
        return header;
    }
}
