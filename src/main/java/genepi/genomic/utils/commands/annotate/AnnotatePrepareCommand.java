package genepi.genomic.utils.commands.annotate;

import java.util.HashSet;
import java.util.Set;

import genepi.genomic.utils.commands.csv.writer.LineWriter;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.gff.Gff3Codec;
import htsjdk.tribble.gff.Gff3Feature;
import htsjdk.tribble.readers.LineIterator;
import picocli.CommandLine.Option;

public class AnnotatePrepareCommand {

	@Option(names = { "--input" }, description = "Input filename (gff3)", required = true)
	private String input;

	@Option(names = { "--output" }, description = "Input filename (csv)", required = true)
	private String output;

	public void setInput(String input) {
		this.input = input;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public Integer call() throws Exception {

		LineWriter writer = new LineWriter(output);
		writer.write("GENE_CHROMOSOME	GENE_START	GENE_END	GENE_NAME");

		Set<String> allowedTypes = new HashSet<String>();
		allowedTypes.add(output);
		allowedTypes.add("protein_coding");
		allowedTypes.add("IG_C_gene");
		allowedTypes.add("IG_D_gene");
		allowedTypes.add("IG_J_gene");
		allowedTypes.add("IG_V_gene");
		allowedTypes.add("TR_C_gene");
		allowedTypes.add("TR_D_gene");
		allowedTypes.add("TR_J_gene");
		allowedTypes.add("TR_V_gene");

		System.out.println("Processing gff3 file '" + input + "'...");

		final AbstractFeatureReader<Gff3Feature, LineIterator> reader = AbstractFeatureReader.getFeatureReader(input,
				null, new Gff3Codec(Gff3Codec.DecodeDepth.SHALLOW), false);
		int countTotalFeatures = 0;
		int countUsedFeatures = 0;
		for (final Gff3Feature feature : reader.iterator()) {
			countTotalFeatures++;
			String type = feature.getAttribute("gene_type").get(0);
			if (feature.getType().equalsIgnoreCase("gene") && allowedTypes.contains(type.toLowerCase())) {
				String chr = feature.getContig().replace("chr", "");
				int start = feature.getStart();
				int end = feature.getEnd();
				String name = feature.getAttribute("gene_name").get(0);
				writer.write(chr + "\t" + start + "\t" + end + "\t" + name);
				countUsedFeatures++;
			}

		}
		reader.close();
		writer.close();
		System.out.println("Processed " + countTotalFeatures + "features.");
		System.out.println("Wrote " + countUsedFeatures + " features to file '" + output + "'.");

		return 0;

	}

}
