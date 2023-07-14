package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VcfChunker implements IChunker {

    private File file;
    private IVariantReader reader;
    private IVariantWriter writer;
    private IManifestWriter mWriter;

    @Override
    public void setReader(IVariantReader reader) {
        this.reader = reader;
    }

    @Override
    public void setWriter(IVariantWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setmWriter(IManifestWriter writer) {
        this.mWriter = writer;
    }

    @Override
    public void executes() {

    }

    public void chunkByRegion(int region, String filename) {

        int start = 0;
        int end = region;
        int chunkNumber = 1;
        int lines = 0;

            List<Variant> variants = new ArrayList<>();
            int chrom = 0, numberVariants = 0, numberSamples = 0;
            String path = "";
            while (reader.next()) {
                Variant v = reader.getVariant();
                if (v.getPosition() >= start && v.getPosition() <= end) {
                    variants.add(v);
                    chrom = Integer.parseInt(v.getChromosome());
                    numberVariants = variants.size();
                    numberSamples = reader.getNumberOfAllSamples();
                    path = reader.getFile().toString();
                } else {
                    if(numberVariants != 0) {
                        mWriter.write("test-data/" + filename + ".txt", chrom, start, end, path, numberVariants, numberSamples, chunkNumber);
                    }
                    variants.clear();
                    variants.add(v); //ist hier, da, wenn die Grenze erreicht wird das else ausgelöst wird und eine Variante sonst verloren geht (gibt wahrscheinlich eine bessere Lösung)
                    numberVariants = 0;
                    start = end + 1;
                    end = start - 1 + region;
                    chunkNumber++;
                }
            }
            // Verarbeitung und Schreiben der letzten Variante außerhalb der Schleife --> gleiches Thema wie beim variants.add(v) in der else letzte würde verloren gehen
            if (!variants.isEmpty()) {
                numberVariants = variants.size();
                mWriter.write("test-data/Manifest.txt", chrom, start, end, path, numberVariants, numberSamples, chunkNumber);
            }
    }

    public void chunkByVariants(int limit){
        int i = 0;
        List<Variant> variants = new ArrayList<>();

        while (i < limit) {
            reader.next();
            variants.add(reader.getVariant());
            i++;
        }
    }

    public int getLinesWritten(){
        return mWriter.getLinesWritten();
    }

    @Override
    public List<VcfChunker> getChunks() {
        return null;
    }
}
