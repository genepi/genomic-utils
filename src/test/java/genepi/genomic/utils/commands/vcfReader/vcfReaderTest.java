package genepi.genomic.utils.commands.vcfReader;

import genepi.genomic.utils.commands.chunker.Genotype;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class vcfReaderTest {

    @Test
    public void testCompleteFile() {

        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        while (reader.next()) {
            reader.next();
        }

        assertEquals(51, reader.getNumberOfAllSamples());
        assertEquals(7824, reader.getNumberOfCurrentVariants());
        assertEquals(7824, reader.getNumberOfAllVariants());
    }

    @Test
    public void test3rdChromAndPosOutput(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();
        reader.next();
        reader.next();

        assertEquals("20 74347", reader.getVariant().getChromosome() + " " + reader.getVariant().getPosition());
    }

    @Test
    public void testNullWithoutNextCall(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        assertEquals(null, reader.getVariant());
    }

    @Test
    public void testFormat(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("[mz]", reader.getVariant().getFormat().toString());
    }

    @Test
    public void testQual(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("100.0", reader.getVariant().getQual());
    }

    @Test
    public void testGenotype(){
        File file = new File("test-data/data.vcf");
        VcfReader reader = new VcfReader(file);

        reader.next();

        assertEquals("C|C", reader.getVariant().getGenotypes().get(0).getGenotype());
    }


}
