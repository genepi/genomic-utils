package genepi.genomic.utils;

import genepi.genomic.utils.commands.VersionCommand;
import genepi.genomic.utils.commands.annotate.AnnotateCommand;
import genepi.genomic.utils.commands.annotate.AnnotateGenesCommand;
import genepi.genomic.utils.commands.annotate.AnnotatePrepareCommand;
import genepi.genomic.utils.commands.bgen.BgenChunkCommand;
import genepi.genomic.utils.commands.csv.CsvConcatCommand;
import genepi.genomic.utils.commands.csv.CsvFilterCommand;
import genepi.genomic.utils.commands.csv.CsvToBedCommand;
import genepi.genomic.utils.commands.gwas.GwasReportCommand;
import genepi.genomic.utils.commands.gwas.GwasReportIndexCommand;
import genepi.genomic.utils.commands.liftover.LiftoverCommand;
import genepi.genomic.utils.commands.regenie.RegenieSplitCommand;
import genepi.genomic.utils.commands.vcf.VcfQualityControlCommand;
import genepi.genomic.utils.commands.vcf.VcfStatisticsCommand;
import genepi.genomic.utils.commands.vcf.VcfToCsvCommand;
import genepi.genomic.utils.commands.vcf.VcfToCsvTransposeCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = App.APP, version = App.VERSION)
public class App {

	public static final String APP = "genomic-utils";

	public static final String VERSION = "0.3.7";

	public static final String URL = "https://github.com/genepi/genomic-utils";

	public static final String COPYRIGHT = "(c) 2023 Lukas Forer and Sebastian Schönherr";

	public static String[] ARGS = new String[0];

	public static void main(String[] args) {

		System.err.println();
		System.err.println(APP + " " + VERSION);
		if (URL != null && !URL.isEmpty()) {
			System.err.println(URL);
		}
		if (COPYRIGHT != null && !COPYRIGHT.isEmpty()) {
			System.err.println(COPYRIGHT);
		}
		System.err.println();

		ARGS = args;

		CommandLine commandLine = new CommandLine(new App());
		commandLine.addSubcommand("annotate", new AnnotateCommand());
		commandLine.addSubcommand("annotate-genes", new AnnotateGenesCommand());
		commandLine.addSubcommand("annotate-prepare", new AnnotatePrepareCommand());
		commandLine.addSubcommand("csv-to-bed", new CsvToBedCommand());
		commandLine.addSubcommand("csv-concat", new CsvConcatCommand());
		commandLine.addSubcommand("csv-filter", new CsvFilterCommand());
		commandLine.addSubcommand("gwas-report", new GwasReportCommand());
		commandLine.addSubcommand("gwas-report-index", new GwasReportIndexCommand());
		commandLine.addSubcommand("liftover", new LiftoverCommand());
		commandLine.addSubcommand("vcf-quality-control", new VcfQualityControlCommand());
		commandLine.addSubcommand("vcf-statistics", new VcfStatisticsCommand());
		commandLine.addSubcommand("vcf-to-csv", new VcfToCsvCommand());
		commandLine.addSubcommand("vcf-to-csv-transpose", new VcfToCsvTransposeCommand());
		commandLine.addSubcommand("bgen-chunk", new BgenChunkCommand());
		commandLine.addSubcommand("regenie-split", new RegenieSplitCommand());
		commandLine.addSubcommand("version", new VersionCommand());

		commandLine.setExecutionStrategy(new CommandLine.RunLast());
		int result = commandLine.execute(args);
		System.exit(result);

	}

}
