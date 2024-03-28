package genepi.genomic.utils.commands.annotate.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import genepi.genomic.utils.commands.annotate.io.GeneAnnotationFileReader.Gene;

public class GeneAnnotationFileReaderTest {

	@Test
	public void testCoordinates() {
			
		char annoSparator = '\t';
		String anno = "test-data/annotation/genes.hg19.v32.csv";
		
		String[] annotationColumns = new String[] {"GENE_NAME"};
		String annoChr = "GENE_CHROMOSOME";
		String annoEnd = "GENE_END";
		String annoStart = "GENE_START";
		GeneAnnotationFileReader annotationReader = new GeneAnnotationFileReader(anno, annotationColumns, annoSparator,
				true, annoChr, annoStart, annoEnd);

		List<Gene> result = annotationReader.query("11", 112835024, 0);
		System.out.println(result);
		assertEquals("NCAM1", result.get(0).get("GENE_NAME"));
		
		
		result = annotationReader.query("3", 136107549, 0);
		System.out.println(result);
		assertEquals("STAG1", result.get(0).get("GENE_NAME"));
		
		result = annotationReader.query("6", 161089307, 0);
		System.out.println(result);
		assertEquals("LPA", result.get(0).get("GENE_NAME"));	

		result = annotationReader.query("16", 68080680, 0);
		System.out.println(result);
		assertEquals("DUS2", result.get(0).get("GENE_NAME"));	

		result = annotationReader.query("6", 163782524, 0);
		System.out.println(result);
		assertEquals("PACRG", result.get(0).get("GENE_NAME"));	
		
		result = annotationReader.query("4", 187140276, 0);
		System.out.println(result);	
		assertEquals("KLKB1", result.get(0).get("GENE_NAME"));	

		
	}
	
	@Test
	public void testCoordinatesHg38() {
			
		char annoSparator = '\t';
		String anno = "test-data/annotation/genes.hg38.csv";
		
		String[] annotationColumns = new String[] {"GENE_NAME"};
		String annoChr = "GENE_CHROMOSOME";
		String annoEnd = "GENE_END";
		String annoStart = "GENE_START";
		GeneAnnotationFileReader annotationReader = new GeneAnnotationFileReader(anno, annotationColumns, annoSparator,
				true, annoChr, annoStart, annoEnd);

		List<Gene> result = annotationReader.query("11", 112835024, 0);
		System.out.println(result);
		assertEquals("NCAM1", result.get(0).get("GENE_NAME"));
		
		
		result = annotationReader.query("3", 136107549, 0);
		System.out.println(result);
		//assertEquals("STAG1", result.get(0).get("GENE_NAME"));
		
		result = annotationReader.query("6", 160664275, 0);
		System.out.println(result);
		assertEquals("LPA", result.get(0).get("GENE_NAME"));	

	}
	
}
