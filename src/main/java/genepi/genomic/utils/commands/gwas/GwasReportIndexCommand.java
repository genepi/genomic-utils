package genepi.genomic.utils.commands.gwas;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.gwas.report.IndexReport;
import lukfor.reports.util.FileUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class GwasReportIndexCommand implements Callable<Integer> {

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private File output;

	@Option(names = { "--title" }, description = "Custom title of report", required = false)
	private String title = null;

	@Option(names = { "--plots" }, description = "Manhattan plots", required = false)
	private String plots = null;

	@Option(names = { "--tab-name" }, description = "Add additional tab to report", required = false)
	private String tab = null;

	@Option(names = { "--tab-links" }, description = "Add additional links to report", required = false)
	private String tabLinks = null;

	@Option(names = { "--names" }, description = "Display Name of files", required = false)
	private String names = null;

	public void setOutput(File output) {
		this.output = output;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public void setPlots(String plots) {
		this.plots = plots;
	}

	public void setTabLinks(String tabLinks) {
		this.tabLinks = tabLinks;
	}

	public void setNames(String names) {
		this.names = names;
	}

	@Override
	public Integer call() throws Exception {

		File assets = initAssets();

		List<String> phenotypes = Arrays.asList(names.split(","));

		List<String> outputFiles = new Vector<String>();
		List<Integer> loci = new Vector<Integer>();

		for (String file : plots.split(",")) {
			File assetFile = copyToAssets(assets, new File(file));
			outputFiles.add(genepi.io.FileUtil.path(assets.getName(), assetFile.getName()));
			loci.add(1);
		}

		List<String> tabLinkFiles = new Vector<String>();
		if (tabLinks != null) {
			for (String file : tabLinks.split(",")) {
				File assetFile = copyToAssets(assets, new File(file));
				tabLinkFiles.add(genepi.io.FileUtil.path(assets.getName(), assetFile.getName()));
			}
		}

		IndexReport indexReport = new IndexReport();
		indexReport.setFiles(outputFiles);
		indexReport.setPhenotypes(phenotypes);
		indexReport.setLoci(loci);
		indexReport.setTitle(title);
		indexReport.setTab(tab != null ? tab : "");
		indexReport.setTabLinks(tabLinkFiles);
		indexReport.saveAsFile(output);

		return 0;

	}

	private File initAssets() {

		String postfix = "_reports";

		String folderFilename = output.getPath().replaceAll(".html", postfix);

		File folderFile = new File(folderFilename);
		if (folderFile.exists()) {
			System.out.println("Clean up files folder.");
			FileUtil.deleteFolder(folderFile);
		}
		folderFile.mkdirs();

		return folderFile;
	}

	private File copyToAssets(File assets, File file) {
		File target = new File(assets, file.getName());
		genepi.io.FileUtil.copy(file.getAbsolutePath(), target.getAbsolutePath());
		return target;
	}
}