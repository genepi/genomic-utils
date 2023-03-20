package genepi.genomic.utils.commands.gwas.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import genepi.genomic.utils.App;
import genepi.genomic.utils.commands.gwas.binner.Variant;
import genepi.genomic.utils.commands.gwas.report.manhattan.ManhattanPlot;
import lukfor.reports.HtmlReport;

public class Report {

	private ManhattanPlot manhattan;

	private List<Variant> topLoci;

	private String title = null;

	public static final String DEFAULT_TITLE = "GWAS Report";

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Variant> getTopLoci() {
		return topLoci;
	}

	public void setTopLoci(List<Variant> topLoci) {
		this.topLoci = topLoci;
	}

	public Report(ManhattanPlot manhattan) {
		this.manhattan = manhattan;
	}

	public void saveAsFile(File file) throws IOException {

		HtmlReport htmlReport = new HtmlReport("/templates");
		htmlReport.setMainFilename("index.html");
		htmlReport.set("application", App.APP);
		htmlReport.set("version", App.VERSION);
		htmlReport.set("manhattan", manhattan);
		htmlReport.set("title", title != null ? title : DEFAULT_TITLE);
		htmlReport.set("peaks", manhattan.getPeaks());
		htmlReport.generate(file);
	}

}
