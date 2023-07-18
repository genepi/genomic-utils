package genepi.genomic.utils.commands.vcfReader;

import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VcfReaderTest {

    @Test
    public void testCompleteFile() throws Exception{

        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        int runs = 0;
        while (reader.next()) {
            runs++;
        }

        assertEquals(51, reader.getNumberOfAllSamples());
        assertEquals(7824, reader.getNumberOfCurrentVariants());
        assertEquals(7824, reader.getNumberOfAllVariants());
        assertEquals(7824, runs);
    }

    @Test
    public void test3rdChromAndPosOutput() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();
        reader.next();
        reader.next();

        assertEquals("20", reader.getVariant().getChromosome());
        assertEquals(74347, reader.getVariant().getPosition());
    }

    @Test
    public void testNullWithoutNextCall() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        assertNull(reader.getVariant());
    }

    @Test
    public void testFormat() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("[mz]", reader.getVariant().getFormat().toString());
    }

    @Test
    public void testQual() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("100.0", reader.getVariant().getQual());
    }

    @Test
    public void testGenotype() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("C|C", reader.getVariant().getGenotypes().get(0).getGenotype());
    }

    @Test (expected = IOException.class)
    public void testInvalidFileException() throws Exception{
        File file = new File("test-data/dataInvalid.vcf");
        VcfReader reader = new VcfReader(file);
    }

    @Test
    public void testExtendedVariables() throws Exception{
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("{mz=no}", reader.getVariant().getGenotypes().get(0).getExtendedAttributes().toString());
    }

    @Test
    public void testVcfGzFile() throws Exception{
        File file = new File ("test-data/data.vcf.gz");
        VcfReader reader = new VcfReader(file);

        reader.next();
    }

    @Test (expected = IOException.class)
    public void testFileNotExistingException() throws Exception{
        File file = new File("test-data/dataNonExistingFile.vcf");
        VcfReader reader = new VcfReader(file);
    }
}
