package genepi.genomic.utils.commands;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command
public class VersionCommand implements Callable<Integer> {

	public Integer call() throws Exception {

		return 0;
	}
}
