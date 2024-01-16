package genepi.genomic.utils.commands.vcf;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ExcelTableWriter;
import genepi.io.table.writer.ITableWriter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class VcfToCsvCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input vcf file", required = true)
	private String input;

	@Option(names = "--output", description = "output file", required = true)
	private String output;

	@Option(names = "--format", description = "output format. xls or csv. default: csv", required = false)
	private String format = "csv";

	@Option(names = "--genotypes", description = "genotypes: `GT` or `DS` or `GT_as_DS`. default: GT", required = false)
	private String genotypes = "GT";

	@Option(names = "--separator", description = "separator for output file. default: ,", required = false)
	private char separator = ',';

	@Option(names = "--quotes", description = "use quote in output file. default: true", required = false)
	private boolean quotes = true;

	public void setInput(String input) {
		this.input = input;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setGenotypes(String genotypes) {
		this.genotypes = genotypes;
	}
	
	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		VCFFileReader reader = new VCFFileReader(new File(input), false);

		List<String> samples = reader.getFileHeader().getGenotypeSamples();

		List<String> columns = new Vector<String>();
		columns.add("id");
		columns.add("chr");
		columns.add("pos");
		columns.add("ref");
		columns.add("alt");
		columns.add("genotyped");
		columns.add("r2");
		columns.addAll(samples);

		ITableWriter writer = null;

		if (format.equals("xls")) {
			writer = new ExcelTableWriter(output);
		} else {
			writer = new CsvTableWriter(output, separator, quotes);
		}

		writer.setColumns(columns.toArray(new String[0]));

		for (VariantContext variant : reader) {
			writer.setString("id", variant.getID());;
			writer.setString("chr", variant.getContig());
			writer.setInteger("pos", variant.getStart());
			writer.setString("ref", variant.getReference().getBaseString());
			String alt = "";
			for (int i = 0; i < variant.getAlternateAlleles().size(); i++) {
				if (!alt.isEmpty()) {
					alt += " ";
				}
				alt += variant.getAlternateAllele(i);
			}
			writer.setString("alt", alt);

			writer.setString("genotyped", variant.getFilters().toString());
			writer.setString("r2", variant.getAttributeAsString("R2", ""));

			for (String sample : samples) {
				if (genotypes.equalsIgnoreCase("GT")) {
					writer.setString(sample, variant.getGenotype(sample).getGenotypeString());
				}else if (genotypes.equalsIgnoreCase("GT_as_DS")){
					writer.setString(sample,""+variant.getGenotype(sample).countAllele(variant.getAlternateAllele(0)));
				} else if (genotypes.equalsIgnoreCase("DS")) {
					String value = variant.getGenotype(sample).getAttributeAsString("DS", "");
					if (value.isEmpty()) {
						value = "" + variant.getGenotype(sample).countAllele(variant.getAlternateAllele(0));
					}
					writer.setString(sample, value);
				}
			}
			writer.next();
		}

		writer.close();
		reader.close();

		return 0;
	}

}