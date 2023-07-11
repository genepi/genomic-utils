package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ReaderTest {

    @Test
    public void testVcfReader() throws Exception {
        File file = new File("src/main/java/genepi/genomic/utils/commands/chunker/testfile/data");
        VcfReader reader = new VcfReader(file);

        //reader.setOutput(createOutputFile());
        assertEquals(file, reader.getFile());
        assertEquals(true, reader.next());
        assertEquals(51, reader.getNumberOfSamples());
        assertEquals(0, reader.getNumberOfVariants());
    }
}
