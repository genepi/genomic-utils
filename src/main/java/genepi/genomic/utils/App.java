package genepi.genomic.utils;

import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.AnnotateCommand;
import genepi.genomic.utils.commands.GwasReportCommand;
import genepi.genomic.utils.commands.LiftoverCommand;
import genepi.genomic.utils.commands.VcfQualityControlCommand;
import genepi.genomic.utils.commands.VcfStatisticsCommand;
import genepi.genomic.utils.commands.VersionCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

public class App {

	public static final String APP = "genomic-utils";

	public static final String VERSION = "0.1.2";

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

		int exitCode = new CommandLine(new DefaultCommand()).execute(args);
		System.exit(exitCode);

	}

	@Command(name = App.APP, version = App.VERSION, subcommands = { AnnotateCommand.class, LiftoverCommand.class,
			GwasReportCommand.class, VcfStatisticsCommand.class, VcfQualityControlCommand.class, VersionCommand.class })
	public static class DefaultCommand implements Callable<Integer> {

		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
