package genepi.genomic.utils.commands.chunker.chunkers;

import genepi.genomic.utils.commands.chunker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VcfChunker implements IChunker {

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
    public void executes() {

    }

    public void chunkByRegion(){

    }

    public void chunkByVariants(int limit){ //TODO: denkfehler es sollen nicht alle ausgegeben werden sondern nur der Start und Endwert (ID oder ähnliches)
        int i = 0;
        List<Variant> variants = new ArrayList<>();

        while (i < limit) {
            reader.next();
            variants.add(reader.getVariant());
            i++;
        }
        this.setManifestWriter(new ManifestWriter(variants));
        manifestWriter.write("/home/marvin/Desktop/Manifest.txt");
    }

    @Override
    public List<VcfChunker> getChunks() {
        return null;
    }
}
