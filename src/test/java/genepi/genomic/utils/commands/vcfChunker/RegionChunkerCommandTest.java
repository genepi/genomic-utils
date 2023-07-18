package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.VcfChunkerCommand;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RegionChunkerCommandTest {

    @Test
    public void testVcfChunkerByRegionSmallFile() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/dataSmall.vcf");
        input.add("test-data/data.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);
        command.setChunkBySize(true);

        assertEquals(14, (int) (command.call()));
    }

    @Test
    public void testVcfChunkerByRegionSmallFileWriter() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/dataSmall.vcf");
        input.add("test-data/data.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);
        command.setChunkBySize(true);

        assertEquals(14, (int) (command.call()));
    }

    @Test
    public void testVcfChunkerByBigFile() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        List<String> input = new ArrayList<>();

        input.add("test-data/data.vcf");

        command.setInput(input);
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);
        command.setChunkBySize(true);

        assertEquals(null, command.call());
    }
}
