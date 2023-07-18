package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.ManifestWriter;
import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RegionChunkerTest {

    @Test
    public void testVcfRegionChunkerSmallFileWriter() throws Exception {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(manifestWriter);
        chunker.setSize(100);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(6, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerSmallFile() throws Exception {
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(6, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerOnlyChunk4() throws Exception{
        File file = new File("test-data/dataOnlyChunk4.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(1, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerVariantAtChunkLimit() throws Exception{
        File file = new File("test-data/dataAllLimits.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerBigFile() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(5000000);
        chunker.executes();

        assertEquals(13, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerEmptyChunk() throws Exception{
        File file = new File("test-data/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(6, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerStringChromosome() throws Exception{
        File file = new File("test-data/dataStringChromosome.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(3, chunker.getNumberChunks());
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyVariant() throws Exception {
        File file = new File("test-data/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyFile() throws Exception {
        File file = new File("test-data/dataEmpty.vcf");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerGZFile() throws Exception {
        File file = new File("test-data/data.vcf.gz");
        VcfReader reader = new VcfReader(file);
        RegionChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(5000000);
        chunker.executes();

        assertEquals(13, chunker.getNumberChunks());
        reader.close();
    }
}
