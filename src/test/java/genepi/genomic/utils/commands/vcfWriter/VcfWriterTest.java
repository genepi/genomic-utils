package genepi.genomic.utils.commands.vcfWriter;

import genepi.genomic.utils.commands.chunker.IVariantReader;
import genepi.genomic.utils.commands.chunker.Variant;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import genepi.genomic.utils.commands.chunker.vcf.VcfWriter;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VcfWriterTest {

    //TODO: add assertEquals to tests
    @Test
    public void testVcfWriterSmallFile() throws Exception{

        File file = new File("test-data/chunker/dataSmall.vcf");
        IVariantReader reader = new VcfReader(file);

        List<Variant> variants = new ArrayList<>();
        while (reader.next()){
            variants.add(reader.getVariant());
        }
        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader(), "outputTest.vcf");
        vcfWriter.setVariants(variants);
        vcfWriter.write();

    }
    @Test
    public void testVcfWriterBigFile() throws Exception{

        File file = new File("test-data/chunker/data.vcf");
        IVariantReader reader = new VcfReader(file);

        List<Variant> variants = new ArrayList<>();
        while (reader.next()){
            variants.add(reader.getVariant());
        }
        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader(), "outputTest.vcf");
        vcfWriter.setVariants(variants);
        vcfWriter.write();
    }
    @Test
    public void testVcfWriterDifferentChromosomes() throws Exception{

        File file = new File("test-data/chunker/dataDifferentChromosomes.vcf");
        IVariantReader reader = new VcfReader(file);

        List<Variant> variants = new ArrayList<>();
        while (reader.next()){
            variants.add(reader.getVariant());
        }
        VcfWriter vcfWriter = new VcfWriter((VCFHeader) reader.getHeader(), "outputTest.vcf");
        vcfWriter.setVariants(variants);
        vcfWriter.write();
    }
}
