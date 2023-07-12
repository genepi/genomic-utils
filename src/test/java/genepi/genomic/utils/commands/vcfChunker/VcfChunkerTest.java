package genepi.genomic.utils.commands.vcfChunker;

import genepi.genomic.utils.commands.chunker.chunkers.VcfChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class VcfChunkerTest {

    File file = new File("/test-data/data.vcf");
    VcfReader reader = new VcfReader(file);

    @Test
    public void testVcfReader() throws Exception {


//        reader.setOutput(createOutputFile());
        reader.next();

        assertEquals(51, reader.getNumberOfAllSamples());
        assertEquals(1, reader.getNumberOfCurrentVariants());
    }

    @Test
    public void testManifestWriter() throws Exception {



//        ManifestWriter mWriter = new ManifestWriter(variants);
//        mWriter.write("/home/marvin/Desktop/Manifest.txt");

//        assertEquals(7823, mWriter.getNumberOfVariants());
    }

    @Test
    public void testVcfChunker() throws Exception {
        VcfChunker chunker = new VcfChunker();
        chunker.setReader(this.reader);

        chunker.chunkByVariants(5);

    }
}
