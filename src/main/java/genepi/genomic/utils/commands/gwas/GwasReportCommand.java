package genepi.genomic.utils.commands.gwas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.gwas.binner.Binner;
import genepi.genomic.utils.commands.gwas.binner.Variant;
import genepi.genomic.utils.commands.gwas.report.IndexReport;
import genepi.genomic.utils.commands.gwas.report.Report;
import genepi.genomic.utils.commands.gwas.report.manhattan.ManhattanPlot;
import genepi.genomic.utils.commands.gwas.report.manhattan.ManhattanPlotWriter;
import genepi.genomic.utils.commands.gwas.util.AnnotationType;
import genepi.genomic.utils.commands.gwas.util.BinningAlgorithm;
import genepi.genomic.utils.commands.gwas.util.IndexCreationMode;
import genepi.genomic.utils.commands.gwas.util.OutputFormat;
import genepi.genomic.utils.commands.gwas.util.PValFormat;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.reader.ITableReader;
import lukfor.reports.util.FileUtil;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command
public class GwasReportCommand implements Callable<Integer> {

	@Parameters(description = "Set files")
	private List<File> files;

	@Option(names = {
			"--chr" }, description = "Chromosome column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String chr = "CHROM";

	@Option(names = { "--position",
			"--pos" }, description = "Position column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String position = "GENPOS";

	@Option(names = { "--pvalue",
			"--pval" }, description = "PValue column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String pval = "LOG10P";

	@Option(names = { "--title" }, description = "Custom title of report", required = false)
	private String title = null;

	@Option(names = { "--pvalue-format",
			"--pval-format" }, description = "Pval format. Possible values: ${COMPLETION-CANDIDATES}", required = false, showDefaultValue = Visibility.ALWAYS)
	private PValFormat pvalFormat = PValFormat.NEG_LOG_PVAL;

	@Option(names = {
			"--binning" }, description = "Binning Algorithm. Possible values: ${COMPLETION-CANDIDATES}", required = false, showDefaultValue = Visibility.ALWAYS)
	private BinningAlgorithm binning = BinningAlgorithm.BIN_TO_POINTS_AND_LINES;

	@Option(names = {
			"--annotation" }, description = "Show annotation labels in plot. Possible values: ${COMPLETION-CANDIDATES}", required = false, showDefaultValue = Visibility.ALWAYS)
	private AnnotationType annotation = AnnotationType.NONE;

	@Option(names = {
			"--sep" }, description = "Separator of input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private char separator = '\t';

	@Option(names = {
			"--ref" }, description = "Ref allele column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String ref = "ALLELE0";

	@Option(names = {
			"--alt" }, description = "Alt allele column in input file (effect allele)", required = false, showDefaultValue = Visibility.ALWAYS)
	private String alt = "ALLELE1";

	@Option(names = {
			"--beta" }, description = "Beta column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String beta = "BETA";

	@Option(names = {
			"--rsid" }, description = "RsID column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String rsid = "ID";

	@Option(names = {
			"--gene" }, description = "Gene column in input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String gene = null;

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private File output;

	@Option(names = {
			"--format" }, description = "Output format. Possible values: ${COMPLETION-CANDIDATES}", required = false, showDefaultValue = Visibility.ALWAYS)
	private OutputFormat format = OutputFormat.HTML;

	@Option(names = { "--tab-name" }, description = "Add additional tab to report", required = false)
	private String tab = null;

	@Option(names = { "--tab-links" }, description = "Add additional links to report", required = false)
	private String tabLinks = null;

	@Option(names = { "--names" }, description = "Display Name of files", required = false)
	private String names = null;

	@Option(names = {
			"--peak-pval-threshold" }, description = "Pval threshold for peak detection", required = false, showDefaultValue = Visibility.ALWAYS)
	private double peakPvalThreshold = Binner.DEFAULT_PEAK_PVAL_THRESHOLD;

	@Option(names = {
			"--peak-variant-Counting-pval-threshold" }, description = "peak-variant-Counting-pval-threshold", required = false, showDefaultValue = Visibility.ALWAYS)
	private double peakVariantCountingPvalThreshold = Binner.DEFAULT_PEAK_VARIANT_COUNTING_PVAL_THRESHOLD;

	@Option(names = {
			"--max-annotations" }, description = "maximal number of annotations", required = false, showDefaultValue = Visibility.ALWAYS)
	private int maxAnnotations = 20;

	@Option(names = {
			"--index" }, description = "Create index html file. Possible values: ${COMPLETION-CANDIDATES}", required = false)
	private IndexCreationMode index = IndexCreationMode.AUTO;

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public void setFiles(File... files) {
		this.files = Arrays.asList(files);
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setPval(String pval) {
		this.pval = pval;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public void setPvalFormat(PValFormat pvalFormat) {
		this.pvalFormat = pvalFormat;
	}

	public void setBinning(BinningAlgorithm binning) {
		this.binning = binning;
	}

	public void setAnnotation(AnnotationType annotation) {
		this.annotation = annotation;
	}

	public void setRsid(String rsid) {
		this.rsid = rsid;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setBeta(String beta) {
		this.beta = beta;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFormat(OutputFormat format) {
		this.format = format;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public void setTabLinks(String tabLinks) {
		this.tabLinks = tabLinks;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public void setPeakPvalThreshold(double peakPvalThreshold) {
		this.peakPvalThreshold = peakPvalThreshold;
	}

	public void setPeakVariantCountingPvalThreshold(double peakVariantCountingPvalThreshold) {
		this.peakVariantCountingPvalThreshold = peakVariantCountingPvalThreshold;
	}

	public void setIndex(IndexCreationMode index) {
		this.index = index;
	}

	@Override
	public Integer call() throws Exception {

		if (files.isEmpty()) {
			System.out.println("No input files provided.");
			return 1;
		}

		if (files.size() == 1 && index == IndexCreationMode.AUTO) {
			ManhattanPlot result = createHtmlReport(title, files.get(0), output);
			if (result != null) {
				return 0;
			} else {
				return 1;
			}
		} else {

			File assets = initAssets();

			List<String> phenotypes = new Vector<String>();
			if (names != null) {
				phenotypes = Arrays.asList(names.split(","));
				if (files.size() != phenotypes.size()) {
					System.out.println(
							"Error: Provided " + files.size() + " files, but " + phenotypes.size() + " phenotypes");
					return 1;
				}
			} else {
				for (File file : files) {
					phenotypes.add(file.getName());
				}
			}

			List<String> tabLinkFiles = new Vector<String>();
			if (tabLinks != null) {
				for (String file : tabLinks.split(",")) {
					File assetFile = copyToAssets(assets, new File(file));
					tabLinkFiles.add(genepi.io.FileUtil.path(assets.getName(), assetFile.getName()));
				}
			}

			List<String> outputFiles = new Vector<String>();
			List<Integer> loci = new Vector<Integer>();

			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				String fileName = file.getName() + ".plot.html";
				File fileOutput = new File(assets, fileName);
				ManhattanPlot result = createHtmlReport(phenotypes.get(i), file, fileOutput);
				if (result == null) {
					return 1;
				}
				outputFiles.add(genepi.io.FileUtil.path(assets.getName(), fileName));
				loci.add(result.getPeaks().size());
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

	}

	private ManhattanPlot createHtmlReport(String title, File input, File output) throws IOException {
		System.out.println("Process file '" + input + "'...");

		CsvTableReader reader = new CsvTableReader(input.getAbsolutePath(), separator, true);

		if (!reader.hasColumn(chr)) {
			System.out.println("Error: Column '" + chr + "' not found.");
			return null;
		}

		if (!reader.hasColumn(chr)) {
			System.out.println("Error: Column '" + position + "' not found.");
			return null;
		}

		if (!reader.hasColumn(chr)) {
			System.out.println("Error: Column '" + pval + "' not found.");
			return null;
		}

		rsid = checkColumn(reader, rsid, "rsid");
		beta = checkColumn(reader, beta, "beta");
		gene = checkColumn(reader, gene, "gene");
		alt = checkColumn(reader, alt, "alt");
		ref = checkColumn(reader, ref, "ref");

		Binner binner = new Binner(binning);
		binner.setPeakPvalThreshold(peakPvalThreshold);
		binner.setPeakVariantCountingPvalThreshold(peakVariantCountingPvalThreshold);

		int count = 0;
		while (reader.next()) {
			count++;
			try {
				Variant variant = new Variant();
				variant.chrom = reader.getString(chr);
				variant.pos = reader.getInteger(position);
				if (pvalFormat == PValFormat.PVAL) {
					variant.pval = -Math.log10(reader.getDouble(pval));
				} else {
					variant.pval = reader.getDouble(pval);
				}
				if (rsid != null && !rsid.isEmpty()) {
					variant.id = reader.getString(rsid);
				}
				if (beta != null && !beta.isEmpty()) {
					variant.beta = reader.getString(beta);
				}
				if (gene != null && !gene.isEmpty()) {
					variant.gene = reader.getString(gene);
				}
				if (alt != null && !alt.isEmpty()) {
					variant.alt = reader.getString(alt);
				}
				if (ref != null && !ref.isEmpty()) {
					variant.ref = reader.getString(ref);
				}

				binner.process_variant(variant);
			} catch (Exception e) {
				System.out.println("Ignore variant " + count + ". Error during parsing.");
			}
		}

		binner.close();

		reader.close();

		System.out.println("Processed " + count + " variants.");

		ManhattanPlot data = new ManhattanPlot(binning);
		data.setBins(binner.getBins());
		data.setAnnotation(annotation);
		data.setPeaks(new ArrayList<Variant>(binner.getPeaks()));
		data.setUnbinnedVariants(new ArrayList<Variant>(binner.getUnbinnedVariants()));
		data.setGenomwideSignificanceLine(peakVariantCountingPvalThreshold);
		data.setSuggestiveSignificanceLine(peakPvalThreshold);
		data.setMaxAnnotations(maxAnnotations);

		if (format == OutputFormat.HTML) {
			Report report = new Report(data);
			if (title != null && !title.isEmpty()) {
				report.setTitle(title);
			}
			report.saveAsFile(output);
		}

		if (format == OutputFormat.CSV) {
			ManhattanPlotWriter writer = new ManhattanPlotWriter(data);
			writer.setBinningAlgorithm(binning);
			writer.setChr(chr);
			writer.setPosition(position);
			writer.setPval(pval);
			writer.setRsid(rsid);
			writer.setRef(ref);
			writer.setAlt(alt);
			writer.setBeta(beta);
			writer.setGene(gene);
			writer.saveAsFile(output);
			return data;
		}

		if (format == OutputFormat.JSON) {
			System.out.println("Not yet implemented.");
			return null;
		}

		return data;
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

	private String checkColumn(ITableReader reader, String column, String param) {
		if (column != null && !column.isEmpty()) {
			if (reader.hasColumn(column)) {
				return column;
			} else {
				System.out.println(
						"Warning: Column '" + column + "' not found. Use '--" + param + " <COLUMN>' to change it.");
			}
		}
		return null;
	}

}
