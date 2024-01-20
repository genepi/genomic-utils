package genepi.genomic.utils.commands.vcf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ExcelTableWriter;
import genepi.io.table.writer.ITableWriter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class VcfToCsvTransposeCommand implements Callable<Integer> {

	private static final String SAMPLE_COLUMN = "sample";

	@Option(names = "--input", description = "input vcf file", required = true)
	private String input;

	@Option(names = "--output", description = "output file", required = true)
	private String output;

	@Option(names = "--format", description = "output format. xls or csv. default: csv", required = false)
	private String format = "csv";

	@Option(names = "--genotypes", description = "genotypes: `GT` or `DS` or `GT_as_DS`. default: GT", required = false)
	private String genotypes = "GT";

	@Option(names = "--name", description = "column name. id or pos. default: pos", required = false)
	private String name = "pos";

	@Option(names = "--chunk-size", description = "column name. id or pos. default: 5000", required = false)
	private int chunk = 100;

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

	public void setQuotes(boolean quotes) {
		this.quotes = quotes;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	@Override
	public Integer call() throws Exception {

		assert (input != null);
		assert (output != null);

		long start = System.currentTimeMillis();
		
		VCFFileReader reader = new VCFFileReader(new File(input), false);

		List<String> samples = reader.getFileHeader().getGenotypeSamples();

		Map<String, List<String>> samplesGenotypes = new HashMap<String, List<String>>();
		for (String sample : samples) {
			samplesGenotypes.put(sample, new Vector<String>());
		}

		List<String> parts = new Vector<String>();

		List<String> snps = new Vector<String>();
		for (VariantContext variant : reader) {
			String snp = null;
			if (name.equalsIgnoreCase("id")) {
				snp = variant.getID();
			} else {
				snp = variant.getContig() + ":" + variant.getStart() + "_" + variant.getReference() + "_"
						+ variant.getAlternateAlleles();
			}
			if (!snps.contains(snp)) {
				snps.add(snp);

				for (String sample : samples) {
					String value = "";
					if (genotypes.equalsIgnoreCase("GT")) {
						value = variant.getGenotype(sample).getGenotypeString();
					}else if (genotypes.equalsIgnoreCase("GT_as_DS")){
						value = "" + variant.getGenotype(sample).countAllele(variant.getAlternateAllele(0));
					} else if (genotypes.equalsIgnoreCase("DS")) {
						value = variant.getGenotype(sample).getAttributeAsString("DS", "");
						if (value.isEmpty()) {
							value = "" + variant.getGenotype(sample).countAllele(variant.getAlternateAllele(0));
						}
					} else {
						throw new RuntimeException("Unknown genotypes option.");
					}
					samplesGenotypes.get(sample).add(value);
				}
			}

			if (snps.size() >= chunk) {
				writePartFile(output, parts, samples, snps, samplesGenotypes);
			}

		}
		reader.close();

		if (!snps.isEmpty()) {
			writePartFile(output, parts, samples, snps, samplesGenotypes);
		}

		System.out.println("Merge part files....");
		mergePartFiles(output, format, parts);
		System.out.println("Files merged.");
		
		long end = System.currentTimeMillis();
		
		System.out.println("Task performed in " + ((end - start) / 1000) + " sec.");
		
		return 0;
	}

	private void mergePartFiles(String output, String format, List<String> parts) {

		ITableWriter writer = null;

		if (format.equals("xls")) {
			writer = new ExcelTableWriter(output);
		} else {
			writer = new CsvTableWriter(output, separator, quotes);
		}

		ITableReader[] readers = new ITableReader[parts.size()];

		List<String> columns = new Vector<String>();
		columns.add(SAMPLE_COLUMN);

		for (int i = 0; i < parts.size(); i++) {
			CsvTableReader reader = new CsvTableReader(parts.get(i), separator);
			for (String column : reader.getColumns()) {
				if (!column.equals(SAMPLE_COLUMN)) {
					columns.add(column);
				}
			}
			readers[i] = reader;
		}
		writer.setColumns(columns.toArray(new String[0]));

		while (readers[0].next()) {
			for (int i = 1; i < readers.length; i++) {
				readers[i].next();
			}
			for (ITableReader reader : readers) {
				for (String column : reader.getColumns()) {
					writer.setString(column, reader.getString(column));
				}
			}
			writer.next();
		}

		for (ITableReader reader : readers) {
			reader.close();
		}
		writer.close();

		//clean up
		for (int i = 0; i < parts.size(); i++) {
			FileUtil.deleteFile(parts.get(i));
		}
		
		
	}

	protected void writePartFile(String output, List<String> parts, List<String> samples, List<String> snps,
			Map<String, List<String>> samplesGenotypes) {
		String partName = output + ".part_" + parts.size();
		writeToFile(partName, samples, snps, samplesGenotypes);
		System.out.println("Wrote " + snps.size() + " variants to file '" + partName + "'.");
		snps.clear();
		samplesGenotypes.clear();
		for (String sample : samples) {
			samplesGenotypes.put(sample, new Vector<String>());
		}		
		parts.add(partName);
	}

	protected void writeToFile(String output, List<String> samples, List<String> snps,
			Map<String, List<String>> samplesGenotypes) {
		List<String> columns = new Vector<String>();
		columns.add(SAMPLE_COLUMN);
		columns.addAll(snps);

		ITableWriter writer = new CsvTableWriter(output, separator, quotes);
		writer.setColumns(columns.toArray(new String[0]));

		for (String sample : samples) {
			writer.setString(SAMPLE_COLUMN, sample);
			for (int i = 0; i < snps.size(); i++) {
				String snp = snps.get(i);
				writer.setString(snp, samplesGenotypes.get(sample).get(i));
			}
			writer.next();
		}

		writer.close();
	}

}