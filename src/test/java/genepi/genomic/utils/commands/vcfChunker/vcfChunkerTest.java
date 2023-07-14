package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.ManifestWriter;
import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import htsjdk.tribble.TribbleException;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class vcfChunkerTest {

    @Test
    public void testVcfChunkerByRegionSmallFile() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter();
        VcfChunker chunker = new VcfChunker();
        chunker.setReader(reader);
        chunker.setmWriter(mWriter);
        chunker.chunkByRegion(100, "Manifest");

        assertEquals(4, chunker.getLinesWritten());
    }
    @Test
    public void testVcfChunkerByRegionBigFile() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter();
        VcfChunker chunker = new VcfChunker();
        chunker.setReader(reader);
        chunker.setmWriter(mWriter);
        chunker.chunkByRegion(5000000, "Manifest");

        assertEquals(14, chunker.getLinesWritten());
    }

    @Test
    public void testVcfChunkerByRegionEmptyChunk() {
        File file = new File("test-data/dataWithEmptyChunk.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter();
        VcfChunker chunker = new VcfChunker();
        chunker.setReader(reader);
        chunker.setmWriter(mWriter);
        chunker.chunkByRegion(100, "Manifest");

        assertEquals(4, chunker.getLinesWritten());
    }

    @Test (expected = TribbleException.class)
    public void testVcfChunkerByRegionEmptyVariant() {
        File file = new File("test-data/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter();
        VcfChunker chunker = new VcfChunker();
        chunker.setReader(reader);
        chunker.setmWriter(mWriter);

        chunker.chunkByRegion(100, "Manifest");
    }
}
