package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.VcfChunkerCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegionChunkerCommandTest {
    @Test
    public void testVcfChunkerByRegionSmallFile() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        command.setInput("test-data/dataSmall.vcf");
        command.setOutput("Manifest.txt");
        command.setChunksize(100);

        assertEquals(null, command.call());
    }

    @Test
    public void testVcfChunkerByBigFile() throws Exception {
        VcfChunkerCommand command = new VcfChunkerCommand();

        command.setInput("test-data/data.vcf");
        command.setOutput("Manifest.txt");
        command.setChunksize(5000000);

        assertEquals(null, command.call());
    }
}
