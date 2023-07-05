package genepi.genomic.utils.commands;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = "vcf-statistics", version = App.VERSION)
public class VcfStatisticsCommand implements Callable<Integer> {

	@Option(names = "--input", description = "input", required = true)
	private String input;

	@Option(names = "--name", description = "name", required = true)
	private String name;

	@Option(names = "--output", description = "output", required = true)
	private String output;

	public static void main(String... args) {
		int exitCode = new CommandLine(new VcfStatisticsCommand()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {

		assert (output != null);

		int totalSnps = 0;

		VCFFileReader reader = new VCFFileReader(new File(input), false);
		int samples = reader.getFileHeader().getNGenotypeSamples();
		for (VariantContext snp : reader) {
			totalSnps++;
		}

		reader.close();

		// append to existing file
		boolean newFile = false;
		if (!new File(output).exists()) {
			newFile = true;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(output, true));
		if (newFile) {
			writer.write("name samples snps");
		}
		writer.write("\n" + name + " " + samples + " " + totalSnps);
		writer.close();

		return 0;
	}

}