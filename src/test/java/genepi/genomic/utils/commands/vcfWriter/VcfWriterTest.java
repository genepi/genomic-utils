package genepi.genomic.utils.commands.vcfWriter;

import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import genepi.genomic.utils.commands.chunker.vcf.VcfWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.Test;
import java.io.File;

import static junit.framework.TestCase.assertEquals;

public class VcfWriterTest {

    @Test
    public void testVcfWriterSmallFile() throws Exception{

        File file = new File("test-data/chunker/dataSmall.vcf");
        IVariantReader reader = new VcfReader(file);

        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader());

        vcfWriter.setOutput("Output.vcf");

        while (reader.next()){
            vcfWriter.setVariant(reader.getVariant());
            vcfWriter.write();
        }
        vcfWriter.close();

        assertEquals(8, vcfWriter.getCalls());
    }

    @Test
    public void testVcfWriterBigFile() throws Exception{

        File file = new File("test-data/chunker/data.vcf");
        IVariantReader reader = new VcfReader(file);

        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader());

        vcfWriter.setOutput("Output.vcf");

        while (reader.next()){
            vcfWriter.setVariant(reader.getVariant());
            vcfWriter.write();
        }
        vcfWriter.close();

        assertEquals(7824, vcfWriter.getCalls());
    }

    @Test
    public void testVcfWriterDifferentChromosomes() throws Exception{

        File file = new File("test-data/chunker/dataDifferentChromosomes.vcf");
        IVariantReader reader = new VcfReader(file);

        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader());

        vcfWriter.setOutput("Output.vcf");

        while (reader.next()){
            vcfWriter.setVariant(reader.getVariant());
            vcfWriter.write();
        }
        vcfWriter.close();

        assertEquals(12, vcfWriter.getCalls());
    }
}
