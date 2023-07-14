package genepi.genomic.utils.commands.vcfReader;

import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import htsjdk.tribble.TribbleException;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class vcfReaderTest {

    @Test
    public void testCompleteFile() {

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
    public void test3rdChromAndPosOutput() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();
        reader.next();
        reader.next();

        assertEquals("20 74347", reader.getVariant().getChromosome() + " " + reader.getVariant().getPosition());
    }

    @Test
    public void testNullWithoutNextCall() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        assertNull(reader.getVariant());
    }

    @Test
    public void testFormat() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("[mz]", reader.getVariant().getFormat().toString());
    }

    @Test
    public void testQual() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("100.0", reader.getVariant().getQual());
    }

    @Test
    public void testGenotype() {
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("C|C", reader.getVariant().getGenotypes().get(0).getGenotype());
    }

    @Test (expected = TribbleException.class)
    public void testInvalidFileException() {
        File file = new File("test-data/dataInvalid.vcf");
        VcfReader reader = new VcfReader(file);
    }

    @Test
    public void testExtendedVariables(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("{mz=no}", reader.getVariant().getGenotypes().get(0).getExtendedAttributes().toString());
    }

    @Test
    public void testVcfGzFile(){
        File file = new File ("test-data/data.vcf.gz");
        VcfReader reader = new VcfReader(file);

        reader.next();
    }

    @Test (expected = TribbleException.class)
    public void testFileNotExistingException(){
        File file = new File("test-data/dataNonExistingFile.vcf");
        VcfReader reader = new VcfReader(file);
    }
}
