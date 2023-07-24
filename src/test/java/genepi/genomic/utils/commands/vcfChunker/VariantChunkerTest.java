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
    public void testVcfVariantChunkerSmallFile() throws Exception {
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(3, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(3, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerSmallFileWriter() throws Exception {
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(3, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(3, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerOnlyChunk4() throws Exception{
        File file = new File("test-data/chunker/dataOnlyChunk4.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(10);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(1, chunker.getChunks().size());
        assertEquals(1, chunker.getChunks().get(0).getVariants());
        assertEquals(0, chunker.getChunks().get(0).getStart());
        assertEquals(10, chunker.getChunks().get(0).getEnd());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerVariantAtChunkLimit() throws Exception{
        File file = new File("test-data/chunker/dataAllLimits.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(4);
        chunker.executes();

        assertEquals(3, chunker.getChunks().size());
        assertEquals(4, chunker.getChunks().get(0).getVariants());
        assertEquals(4, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerBigFile() throws Exception{
        File file = new File("test-data/chunker/data.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(500);
        chunker.executes();

        assertEquals(16, chunker.getChunks().size());
        assertEquals(500, chunker.getChunks().get(0).getVariants());
        assertEquals(500, chunker.getChunks().get(1).getVariants());
        assertEquals(500, chunker.getChunks().get(2).getVariants());
        assertEquals(324, chunker.getChunks().get(15).getVariants());

        reader.close();
    }

    @Test
    public void testVcfVariantChunkerEmptyChunk() throws Exception{
        File file = new File("test-data/chunker/dataSmall.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(2);
        chunker.executes();

        assertEquals(4, chunker.getChunks().size());
        assertEquals(2, chunker.getChunks().get(0).getVariants());
        assertEquals(2, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerStringChromosomeWriter() throws Exception{
        File file = new File("test-data/chunker/dataStringChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        ManifestWriter manifestWriter = new ManifestWriter("Manifest.txt");
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(2);
        chunker.executes();

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();

        assertEquals(7, chunker.getChunks().size());
        assertEquals(2, chunker.getChunks().get(0).getVariants());
        assertEquals(2, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfVariantChunkerEmptyVariant() throws Exception {
        File file = new File("test-data/chunker/dataWithEmptyVariant.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test(expected = IOException.class)
    public void testVcfVariantChunkerEmptyFile() throws Exception {
        File file = new File("test-data/chunker/dataEmpty.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(100);
        chunker.executes();
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerGZFile() throws Exception {
        File file = new File("test-data/chunker/data.vcf.gz");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(2608, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(3, chunker.getChunks().get(1).getVariants());
        assertEquals(3, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerDifferentChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataDifferentChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(3);
        chunker.executes();

        assertEquals(6, chunker.getChunks().size());
        assertEquals(3, chunker.getChunks().get(0).getVariants());
        assertEquals(3, chunker.getChunks().get(1).getVariants());
        assertEquals(2, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

    @Test
    public void testVcfVariantChunkerDifferentStringChromosomes() throws Exception {
        File file = new File("test-data/chunker/dataStringChromosomes.vcf");
        VcfReader reader = new VcfReader(file);
        IChunker chunker = new VariantChunker();
        chunker.setReader(reader);
        chunker.setSize(4);
        chunker.executes();

        assertEquals(5, chunker.getChunks().size());
        assertEquals(4, chunker.getChunks().get(0).getVariants());
        assertEquals(4, chunker.getChunks().get(1).getVariants());
        assertEquals(1, chunker.getChunks().get(2).getVariants());
        reader.close();
    }

}
