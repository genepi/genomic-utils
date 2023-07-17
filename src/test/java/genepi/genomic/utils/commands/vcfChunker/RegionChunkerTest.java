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
    public void testVcfRegionChunkerSmallFile() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(manifestWriter);
        chunker.executes(100);

        assertEquals(6, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerOnlyChunk4(){
        File file = new File("test-data/dataOnlyChunk4.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(manifestWriter);
        chunker.executes(100);

        assertEquals(1, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerVariantAtChunkLimit() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(6, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerBigFile() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(5000000);

        assertEquals(13, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerEmptyChunk() {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(6, chunker.getLinesWritten());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerStringChromosome() {
        File file = new File("test-data/dataStringChromosome.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter mWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(mWriter);
        chunker.executes(100);

        assertEquals(3, chunker.getLinesWritten());
        reader.close();
    }

    @Test(expected = TribbleException.class)
    public void testVcfRegionChunkerEmptyVariant() {
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
    public void testVcfRegionChunkerEmptyFile() {
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
