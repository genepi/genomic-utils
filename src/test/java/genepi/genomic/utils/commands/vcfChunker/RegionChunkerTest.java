package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.ManifestWriter;
import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import htsjdk.tribble.TribbleException;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class RegionChunkerTest {

    @Test
    public void testVcfChunkerByRegionSmallFile() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(manifestWriter);
        chunker.executes(100);

        assertEquals(5, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfChunkerByRegionVariantAtChunkLimit() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(5, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfChunkerByRegionBigFile() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(5000000);

        assertEquals(14, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfChunkerByRegionEmptyChunk() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(5, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfChunkerByRegionStringChromosome() {
        File file = new File("test-data/dataStringChromosome.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(4, chunker.getLinesWritten());
        reader.close();
    }

    @Test(expected = TribbleException.class)
    public void testVcfChunkerByRegionEmptyVariant() {
        File file = new File("test-data/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);

        chunker.executes(100);
        reader.close();
    }

    @Test(expected = TribbleException.class)
    public void testVcfChunkerByRegionEmptyFile() {
        File file = new File("test-data/dataEmpty.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);

        chunker.executes(100);
        reader.close();
    }
}
