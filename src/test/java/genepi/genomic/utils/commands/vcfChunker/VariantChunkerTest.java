package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.IChunker;
import genepi.genomic.utils.commands.chunker.ManifestWriter;
import genepi.genomic.utils.commands.chunker.chunkers.VariantChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VariantChunkerTest {

    @Test
    public void testVcfRegionChunkerSmallFile() throws Exception {
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(3, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerSmallFileWriter() throws Exception {
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setManifestWriter(manifestWriter);
        chunker.setSize(3);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(3, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerOnlyChunk4() throws Exception{
        File file = new File("test-data/chunker/dataOnlyChunk4.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(1, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerVariantAtChunkLimit() throws Exception{
        File file = new File("test-data/chunker/dataAllLimits.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(4);
        chunker.executes();

        assertEquals(3, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerBigFile() throws Exception{
        File file = new File("test-data/chunker/data.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(2608, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerEmptyChunk() throws Exception{
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(2);
        chunker.executes();

        assertEquals(4, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerStringChromosomeWriter() throws Exception{
        File file = new File("test-data/chunker/dataStringChromosome.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setManifestWriter(manifestWriter);
        chunker.setReader(reader);
        chunker.setSize(2);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(7, chunker.getNumberChunks());
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyVariant() throws Exception {
        File file = new File("test-data/chunker/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfRegionChunkerEmptyFile() throws Exception {
        File file = new File("test-data/chunker/dataEmpty.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test
    public void testVcfRegionChunkerGZFile() throws Exception {
        File file = new File("test-data/chunker/data.vcf.gz");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(2608, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfChunkerDifferentChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataDifferentChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(6, chunker.getNumberChunks());
        reader.close();
    }

    @Test
    public void testVcfChunkerDifferentStringChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataStringChromosome.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(4);
        chunker.executes();

        assertEquals(5, chunker.getNumberChunks());
        reader.close();
    }

}
