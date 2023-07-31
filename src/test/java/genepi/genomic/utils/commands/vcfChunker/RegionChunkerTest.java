package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.IChunker;
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
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(1, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerSmallFile() throws Exception {

        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(1, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        assertEquals(0, chunker.getChunks().get(0).getStart());
        assertEquals(100, chunker.getChunks().get(0).getEnd());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());

        reader.close();
    }

    @Test
    public void testVcfRegionChunkerOnlyChunk4() throws Exception {
        File file = new File("test-data/chunker/dataOnlyChunk4.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(1, chunker.getChunks().size());
        assertEquals(1, chunker.getChunks().get(0).getVariants());
        assertEquals(301, chunker.getChunks().get(0).getStart());
        assertEquals(400, chunker.getChunks().get(0).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerVariantAtChunkLimit() throws Exception {
        File file = new File("test-data/chunker/dataAllLimits.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(2, chunker.getChunks().get(0).getVariants());
        assertEquals(2, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerBigFile() throws Exception {
        File file = new File("test-data/chunker/data.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(5000000);
        chunker.executes();

        assertEquals(13, chunker.getChunks().size());
        assertEquals(902, chunker.getChunks().get(0).getVariants());
        assertEquals(766, chunker.getChunks().get(1).getVariants());
        assertEquals(719, chunker.getChunks().get(2).getVariants());
        assertEquals(5000001, chunker.getChunks().get(1).getStart());
        assertEquals(10000000, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerEmptyChunk() throws Exception {
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(1, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerStringChromosome() throws Exception {
        File file = new File("test-data/chunker/dataStringChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(6, chunker.getChunks().get(0).getVariants());
        assertEquals(2, chunker.getChunks().get(1).getVariants());
        assertEquals(1, chunker.getChunks().get(2).getVariants());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyVariant() throws Exception {
        File file = new File("test-data/chunker/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyFile() throws Exception {
        File file = new File("test-data/chunker/dataEmpty.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerGZFile() throws Exception {
        File file = new File("test-data/chunker/data.vcf.gz");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(5000000);
        chunker.executes();

        assertEquals(13, chunker.getChunks().size());
        assertEquals(902, chunker.getChunks().get(0).getVariants());
        assertEquals(766, chunker.getChunks().get(1).getVariants());
        assertEquals(719, chunker.getChunks().get(2).getVariants());
        assertEquals(5000001, chunker.getChunks().get(1).getStart());
        assertEquals(10000000, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerDifferentChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataDifferentChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(8, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(1, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerDifferentStringChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataStringChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new RegionChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(6, chunker.getChunks().get(0).getVariants());
        assertEquals(2, chunker.getChunks().get(1).getVariants());
        assertEquals(1, chunker.getChunks().get(2).getVariants());
        assertEquals(101, chunker.getChunks().get(1).getStart());
        assertEquals(200, chunker.getChunks().get(1).getEnd());
        reader.close();
    }

}
