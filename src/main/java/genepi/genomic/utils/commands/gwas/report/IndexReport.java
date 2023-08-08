package genepi.genomic.utils.commands.gwas.report;

import java.io.File;
import java.util.List;

import genepi.genomic.utils.App;
import lukfor.reports.HtmlReport;

public class IndexReport {

	private List<String> phenotypes;

	private List<String> files;

	private String title;

	private String tab;

	private List<String> tabLinks;

	private List<Integer> loci;

	public void setPhenotypes(List<String> phenotypes) {
		this.phenotypes = phenotypes;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLoci(List<Integer> loci) {
		this.loci = loci;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public void setTabLinks(List<String> tabLinks) {
		this.tabLinks = tabLinks;
	}

	public void saveAsFile(File output) throws Exception {

		HtmlReport report = new HtmlReport("/templates/index");
		report.setMainFilename("report.html");
		report.set("application", "nf-gwas");
		report.set("version", "");
		report.set("project", title);
		report.set("date", "date");
		report.set("phenotypes", phenotypes);
		report.set("files", files);
		report.set("loci", loci);
		report.set("tab", tab == null ? "" : tab);
		report.set("tabLinks", tabLinks);
		report.generate(output);

	}

}
