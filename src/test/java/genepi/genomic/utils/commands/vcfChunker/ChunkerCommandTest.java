package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.VcfChunkerCommand;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ChunkerCommandTest {

    @Test
    public void testVcfChunkerByRegionTwoInputFiles() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

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
        VcfChunkerCommand command = new VcfChunkerCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/dataSmall.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(100);

        assertEquals(0, (int) (command.call()));
        assertEquals(5, command.getNumberChunks());
    }

    @Test
    public void testVcfChunkerByBigFile() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/chunker/data.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);

        assertEquals(0, (int) (command.call()));
        assertEquals(13, command.getNumberChunks());
    }
}
