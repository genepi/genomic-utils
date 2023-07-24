package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.ChunkingStrategy;
import genepi.genomic.utils.commands.chunker.ChunkCommand;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChunkCommandTest {

    @Test
    public void testVcfChunkerByRegionTwoInputFiles() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/data.vcf");
        input.add("test-data/chunker/dataSmall.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);

        assertEquals(0, (int) (command.call()));
        assertEquals(14, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByRegionSmallFile() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/dataSmall.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(100);

        assertEquals(0, (int) (command.call()));
        assertEquals(5, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByRegionBigFile() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/data.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);

        assertEquals(0, (int) (command.call()));
        assertEquals(13, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByRegionDifferentChromosomes() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/dataSmall.vcf");
        input.add("test-data/chunker/dataDifferentChromosomes.vcf");
        input.add("test-data/chunker/dataStringChromosomes.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(100);

        assertEquals(0, (int) (command.call()));
        assertEquals(18, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByVariantTwoInputFiles() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/data.vcf");
        input.add("test-data/chunker/dataSmall.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(500);
        command.setStrategy(ChunkingStrategy.CHUNK_BY_VARIANTS);

        assertEquals(0, (int) (command.call()));
        assertEquals(17, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByVariantDifferentChromosomes() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/dataSmall.vcf");
        input.add("test-data/chunker/dataDifferentChromosomes.vcf");
        input.add("test-data/chunker/dataStringChromosomes.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5);
        command.setStrategy(ChunkingStrategy.CHUNK_BY_VARIANTS);

        assertEquals(0, (int) (command.call()));
        assertEquals(11, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByVariantManyFiles() throws Exception {
        ChunkCommand command = new ChunkCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/data.vcf");
        input.add("test-data/chunker/dataAllLimits.vcf");
        input.add("test-data/chunker/dataDifferentChromosomes.vcf");
        input.add("test-data/chunker/dataSmall.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(500);
        command.setStrategy(ChunkingStrategy.CHUNK_BY_VARIANTS);

        assertEquals(0, (int) (command.call()));
        assertEquals(22, command.getNumberChunks());
    }
}
