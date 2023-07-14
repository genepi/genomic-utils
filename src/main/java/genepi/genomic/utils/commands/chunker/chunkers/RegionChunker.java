package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionChunker implements IChunker {

    private File file;
    private IVariantReader reader;
    private IVariantWriter writer;
    private IManifestWriter manifestWriter;

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
    public void executes(int region) {
        int start = 0;
        int end = region;
        int chunkNumber = 1;
        int lines = 0;

        List<Variant> variants = new ArrayList<>();
        VcfChunk chunk = null;
        List<VcfChunk> chunks = new ArrayList<>();
        String chrom = "";
        int numberVariants = 0, numberSamples = 0;
        String path = "";
        while (reader.next()) {

//            chunkNumber = start / region;
//            if(start % region == 0){
//                chunkNumber = chunkNumber - 1;
//            } //TODO: ALLES fixen


            Variant v = reader.getVariant();
            if (v.getPosition() >= start && v.getPosition() <= end) {
                variants.add(v);
                chrom = v.getChromosome();
                numberVariants = variants.size();
                numberSamples = reader.getNumberOfAllSamples();
                path = reader.getFile().toString();
                chunk = new VcfChunk(chunkNumber, chrom, start, end, numberVariants, numberSamples, new File(path));
            } else {
                if (numberVariants != 0) {
                    chunks.add(chunk);
                }
                variants.clear();
                variants.add(v); //ist hier, da, wenn die Grenze erreicht wird das else ausgelöst wird und eine Variante sonst verloren geht (gibt wahrscheinlich eine bessere Lösung)
                numberVariants = variants.size();
                start = end + 1;
                end = start - 1 + region;
                chunkNumber++;
                chunk = new VcfChunk(chunkNumber, chrom, start, end, numberVariants, numberSamples, new File(path));
            }
        }
        // Verarbeitung und Schreiben der letzten Variante außerhalb der Schleife --> gleiches Thema wie beim variants.add(v) in der else letzte würde verloren gehen
        if (!variants.isEmpty()) {
            chunks.add(chunk);
        }
        manifestWriter.setVcfChunks(chunks);
        manifestWriter.write();
    }

    public int getLinesWritten() {
        int lines = manifestWriter.getLinesWritten();
        manifestWriter.setLinesWritten(0);
        return lines;
    }

    @Override
    public List<RegionChunker> getChunks() {
        return null;
    }
}
