package genepi.genomic.utils.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ExcelTableWriter;
import genepi.io.table.writer.ITableWriter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "vcf-to-csv-transpose", version = App.VERSION)
public class VcfToCsvTransposeCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input vcf file", required = true)
	private String input;

	@Option(names = "--output", description = "output file", required = true)
	private String output;

	@Option(names = "--format", description = "output format. xls or csv. default: csv", required = false)
	private String format = "csv";

	@Option(names = "--genotypes", description = "genotypes: GT or DS", required = false)
	private String genotypes = "GT";

	public void setInput(String input) {
		this.input = input;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		VCFFileReader reader = new VCFFileReader(new File(input), false);

		List<String> samples = reader.getFileHeader().getGenotypeSamples();

		Map<String, List<String>> samplesGenotypes = new HashMap<String, List<String>>();
		for (String sample : samples) {
			samplesGenotypes.put(sample, new Vector<String>());
		}

		List<String> snps = new Vector<String>();

		for (VariantContext variant : reader) {
			String snp = variant.getContig() + ":" + variant.getStart();
			snps.add(snp);

			for (String sample : samples) {
				String value = "";
				if (genotypes.equals("GT")) {
					value = variant.getGenotype(sample).getGenotypeString();
				} else if (genotypes.equals("DS")) {
					value = variant.getGenotype(sample).getAttributeAsString("DS", "");
				}
				samplesGenotypes.get(sample).add(value);
			}

		}

		List<String> columns = new Vector<String>();
		columns.add("sample");
		columns.addAll(snps);

		ITableWriter writer = null;

		if (format.equals("xls")) {
			writer = new ExcelTableWriter(output);
		} else {
			writer = new CsvTableWriter(output);
		}

		writer.setColumns(columns.toArray(new String[0]));
		for (String sample : samples) {
			writer.setString("sample", sample);
			for (int i = 0; i < snps.size(); i++) {
				String snp = snps.get(i);
				writer.setString(snp, samplesGenotypes.get(sample).get(i));
			}
			writer.next();
		}

		writer.close();
		reader.close();

		return 0;
	}

}