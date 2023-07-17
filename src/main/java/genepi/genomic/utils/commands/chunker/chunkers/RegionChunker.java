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
    private List<VcfChunk> chunks;

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
        int chunkNumber = 0;
        VcfChunk chunk;
        List<VcfChunk> chunks = new ArrayList<>();
        String chrom = "";
        int numberVariants = 0, numberSamples = reader.getNumberOfAllSamples();
        String path = reader.getFile().toString();

        while (reader.next()) {
            Variant v = reader.getVariant();

            if (v.getPosition() > end) {
                // Nur Chunks mit Varianten hinzufügen
                if (numberVariants > 0) {
                    chunk = new VcfChunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
                    chunk.setVariants(numberVariants);
                    chunks.add(chunk);
                    chunkNumber++;
                }

                // Überprüfen, ob die Variante zum nächsten Chunk gehört
                while (v.getPosition() > end) {
                    start = end + 1;
                    end = start + region - 1;
                }

                // Chunk-relevante Informationen zurücksetzen
                numberVariants = 0;
                chrom = "";
                numberSamples = reader.getNumberOfAllSamples();
                path = reader.getFile().toString();
            }

            // Überprüfen, ob die Variante innerhalb des aktuellen Bereichs liegt
            if (v.getPosition() >= start && v.getPosition() <= end) {
                // Variante zur aktuellen Chunk-Zählung hinzufügen
                numberVariants++;
                if (chrom.isEmpty()) {
                    chrom = v.getChromosome();
                }
            }
        }

        // Letzten Chunk erstellen und zur Liste hinzufügen, wenn Varianten vorhanden
        if (numberVariants > 0) {
            chunk = new VcfChunk(chunkNumber, chrom, start, end, numberSamples, new File(path));
            chunk.setVariants(numberVariants);
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
    public List<VcfChunk> getChunks() {
        return this.chunks;
    }
}
