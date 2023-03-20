package genepi.genomic.utils.commands;

import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import picocli.CommandLine.Command;

@Command(name = "version", version = App.VERSION)
public class VersionCommand implements Callable<Integer> {

	public Integer call() throws Exception {

		return 0;
	}
}
