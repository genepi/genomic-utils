package genepi.genomic.utils.commands.gwas.report.manhattan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import genepi.genomic.utils.commands.gwas.binner.Bin;
import genepi.genomic.utils.commands.gwas.binner.ChromBin;
import genepi.genomic.utils.commands.gwas.binner.Chromosome;
import genepi.genomic.utils.commands.gwas.binner.Variant;
import genepi.genomic.utils.commands.gwas.util.AnnotationType;
import genepi.genomic.utils.commands.gwas.util.BinningAlgorithm;
import genepi.genomic.utils.commands.gwas.util.PlotlyUtil;

public class ManhattanPlot {

	public static final double DEFAULT_SUGGESTIVE_SIGNIFICANCE_LINE = -Math.log10(1e-5);

	public static final String SUGGESTIVE_SIGNIFICANCE_LINE_COLOR = "#d0d0d0";

	public static final int SUGGESTIVE_SIGNIFICANCE_LINE_WIDTH = 2;

	public static final String SUGGESTIVE_SIGNIFICANCE_LINE_STYLE = "dash";

	public static final double DEFAULT_GENOMEWIDE_SIGNIFICANCE_LINE = -Math.log10(5e-08);

	public static final String GENOMEWIDE_SIGNIFICANCE_LINE_COLOR = "#c86467";

	public static final int GENOMEWIDE_SIGNIFICANCE_LINE_WIDTH = 2;

	public static final String GENOMEWIDE_SIGNIFICANCE_LINE_STYLE = "dash";

	public static final String[] CHROMOSOME_COLORS = new String[] { "#779ECB", "#03254c" };

	public static final double BIN_SIZE = 3_000_000;

	public static final int CHROMOSOME_GAP = 10;

	public static final int POINT_SIZE = 5;

	public static final int HEIGHT = 550;

	private double suggestiveSignificanceLine = DEFAULT_SUGGESTIVE_SIGNIFICANCE_LINE;

	private double genomwideSignificanceLine = DEFAULT_GENOMEWIDE_SIGNIFICANCE_LINE;

	private Map<Integer, ChromBin> bins;

	private BinningAlgorithm binning = BinningAlgorithm.BIN_TO_POINTS_AND_LINES;

	private AnnotationType annotation = AnnotationType.NONE;

	private List<Variant> peaks;

	private int countLines = 0;

	private int countBins = 0;

	private int countPoints = 0;

	public ManhattanPlot(BinningAlgorithm binning) {
		this.binning = binning;
	}

	public void setBins(Map<Integer, ChromBin> bins) {
		this.bins = bins;
	}

	public Map<Integer, ChromBin> getBins() {
		return bins;
	}

	protected ChromBin getBin(String chrom) {
		int index = Chromosome.getOrder(chrom);
		ChromBin bin = bins.get(index);
		if (bin == null) {
			bin = new ChromBin(chrom);
			bins.put(index, bin);
		}
		return bin;
	}

	public void setGenomwideSignificanceLine(double genomwideSignificanceLine) {
		this.genomwideSignificanceLine = genomwideSignificanceLine;
	}

	public void setSuggestiveSignificanceLine(double suggestiveSignificanceLine) {
		this.suggestiveSignificanceLine = suggestiveSignificanceLine;
	}

	public void setPeaks(List<Variant> peaks) {
		for (Variant peak : peaks) {
			ChromBin bin = getBin(peak.chrom);
			bin.addPeakVariant(peak);
		}
		this.peaks = peaks;
	}

	public List<Variant> getPeaks() {
		return peaks;
	}

	public void setUnbinnedVariants(List<Variant> unbinnedVariants) {
		for (Variant variant : unbinnedVariants) {
			ChromBin bin = getBin(variant.chrom);
			bin.addUnbinnedVariant(variant);
		}
	}

	private List<Object> getBinsAsShapes(int chrIndex, long offset, String color) {
		List<Object> shapes = new Vector<>();
		ChromBin bin = bins.get(chrIndex);
		for (Bin singleBin : bin.getBins().values()) {
			countBins += singleBin.qval.size();
			for (Double[] line : singleBin.getLines(binning)) {
				if (!line[0].equals(line[1])) {
					countLines++;
					double x = (singleBin.startpos / BIN_SIZE) + offset;
					Map<String, Object> shape = PlotlyUtil.createLine(x, line[0], x, line[1], color, POINT_SIZE);
					shapes.add(shape);
				}
			}
		}
		return shapes;
	}

	private Map<String, Object> getSingleBinsAsTrace(int chrIndex, long offset, String color) {
		Map<String, Object> trace = new HashMap<String, Object>();

		ChromBin bin = bins.get(chrIndex);
		List<Double> x = new Vector<Double>();
		List<Double> y = new Vector<Double>();
		for (Bin singleBin : bin.getBins().values()) {
			countBins += singleBin.qval.size();
			for (Double[] line : singleBin.getLines(binning)) {
				if (line[0].equals(line[1])) {
					countPoints++;
					double x0 = (singleBin.startpos / BIN_SIZE) + offset;
					x.add(x0);
					y.add(line[0]);
				}
			}
		}
		trace.put("x", x);
		trace.put("y", y);
		trace.put("mode", "markers");
		trace.put("type", "scatter");
		trace.put("name", bin.chrom);

		Map<String, Object> marker = new HashMap<>();
		marker.put("color", color);
		marker.put("size", POINT_SIZE);

		trace.put("marker", marker);
		trace.put("hoverinfo", "none");
		return trace;
	}

	private List<Object> getSignifanceLines() {
		List<Object> shapes = new Vector<>();
		shapes.add(PlotlyUtil.createHorizontalLine(suggestiveSignificanceLine, SUGGESTIVE_SIGNIFICANCE_LINE_COLOR,
				SUGGESTIVE_SIGNIFICANCE_LINE_WIDTH, SUGGESTIVE_SIGNIFICANCE_LINE_STYLE));
		shapes.add(PlotlyUtil.createHorizontalLine(genomwideSignificanceLine, GENOMEWIDE_SIGNIFICANCE_LINE_COLOR,
				GENOMEWIDE_SIGNIFICANCE_LINE_WIDTH, GENOMEWIDE_SIGNIFICANCE_LINE_STYLE));
		return shapes;
	}

	public List<Object> getData() {
		List<Object> traces = new Vector<Object>();
		long offset = 0;
		int index = 0;
		for (int chr : bins.keySet()) {
			String color = CHROMOSOME_COLORS[index % CHROMOSOME_COLORS.length];
			// if (!asShapes) {
			// traces.add(getBinsAsTrace(chr, offset, color));
			// } else {
			traces.add(getSingleBinsAsTrace(chr, offset, color));
			// }
			traces.add(getVariantsAsTrace(chr, offset, color));
			offset = updateOffset(offset, chr);
			index++;
		}
		return traces;
	}

	private List<Object> getAnnotations(AnnotationType annotationType) {
		List<Object> traces = new Vector<Object>();
		long offset = 0;
		for (int chr : bins.keySet()) {
			traces.addAll(getAnnotationsByChromosome(chr, offset, annotationType));
			offset = updateOffset(offset, chr);
		}
		return traces;
	}

	private Map<String, Object> getXAxis() {
		Map<String, Object> axis = new HashMap<>();
		axis.put("fixedrange", true);
		axis.put("tickwidth", 0);
		axis.put("tickmode", "array");
		List<Object> tickvals = new Vector<Object>();
		List<Object> ticktext = new Vector<Object>();
		long offset = 0;
		for (int chr : bins.keySet()) {
			ChromBin bin = bins.get(chr);
			ticktext.add(bin.chrom);
			tickvals.add(offset + bins.get(chr).getLength() / BIN_SIZE / 2);
			offset = updateOffset(offset, chr);
		}
		axis.put("tickvals", tickvals);
		axis.put("ticktext", ticktext);
		axis.put("showgrid", false);

		Map<String, Object> title = new HashMap<>();
		title.put("text", "Chromosome");
		title.put("automargin", false);
		axis.put("title", title);

		return axis;
	}

	private Map<String, Object> getYAxis() {
		Map<String, Object> axis = new HashMap<>();
		axis.put("fixedrange", true);

		Map<String, Object> title = new HashMap<>();
		title.put("text", "-log<sub>10</sub>(<i>P</i>)");
		axis.put("title", title);

		return axis;
	}

	private List<Object> getShapes() {
		List<Object> shapes = new Vector<Object>();
		long offset = 0;
		int index = 0;
		for (int chr : bins.keySet()) {
			String color = CHROMOSOME_COLORS[index % CHROMOSOME_COLORS.length];
			shapes.addAll(getBinsAsShapes(chr, offset, color));
			offset = updateOffset(offset, chr);
			index++;
		}
		System.out.println("Bins: " + countBins + " --> Points: " + countPoints + ", Lines: " + countLines);
		System.out.println("Created " + shapes.size() + " shapes");

		return shapes;
	}

	private long updateOffset(long offset, int chr) {
		offset += (bins.get(chr).getLength() / BIN_SIZE) + CHROMOSOME_GAP;
		return offset;
	}

	private Map<String, Object> getVariantsAsTrace(int chrIndex, long offset, String color) {
		Map<String, Object> trace = new HashMap<String, Object>();
		List<Double> x = new Vector<Double>();
		List<Double> y = new Vector<Double>();
		List<String> text = new Vector<String>();
		ChromBin bin = bins.get(chrIndex);
		for (Variant variant : bin.getUnbinnedVariants()) {
			x.add((variant.pos / BIN_SIZE) + offset);
			y.add(variant.pval);
			text.add(variant.getDetails());

		}
		for (Variant variant : bin.getPeakVariants()) {
			x.add((variant.pos / BIN_SIZE) + offset);
			y.add(variant.pval);
			text.add(variant.getDetails());
		}
		trace.put("x", x);
		trace.put("y", y);
		trace.put("text", text);
		trace.put("mode", "markers");
		trace.put("type", "scatter");
		trace.put("name", bin.chrom);
		trace.put("hovertemplate", "%{text}");

		Map<String, Object> marker = new HashMap<>();
		marker.put("size", POINT_SIZE);

		marker.put("color", color);

		trace.put("marker", marker);
		return trace;
	}

	public List<Object> getAnnotationsByChromosome(int chrIndex, long offset, AnnotationType annotationType) {
		List<Object> annotations = new Vector<Object>();
		ChromBin bin = bins.get(chrIndex);
		for (Variant variant : bin.getPeakVariants()) {
			Map<String, Object> annotation = new HashMap<>();
			annotation.put("x", (variant.pos / BIN_SIZE) + offset);
			annotation.put("y", variant.pval);
			annotation.put("xref", "x");
			annotation.put("yref", "y");
			if (annotationType == AnnotationType.GENE) {
				annotation.put("text", "<i>" + variant.gene + "</i>");
			} else {
				annotation.put("text", variant.getName());
			}
			annotation.put("ax", 0);
			annotation.put("showarrow", true);
			annotation.put("arrowhead", 0);
			annotation.put("bordercolor", "#aaaaaa");
			annotation.put("ay", -40);
			annotations.add(annotation);
		}
		return annotations;
	}

	protected Map<String, Object> getLayout() {
		Map<String, Object> layout = new HashMap<String, Object>();
		layout.put("xaxis", getXAxis());
		layout.put("yaxis", getYAxis());
		layout.put("showlegend", false);
		layout.put("hovermode", "closest");
		if (annotation != AnnotationType.NONE) {
			layout.put("annotations", getAnnotations(annotation));
		}
		// layout.put("autosize", false);
		// layout.put("width", 1300);
		// layout.put("height", getHeight());
		List<Object> shapes = new Vector<>();

		shapes.addAll(getShapes());

		shapes.addAll(getSignifanceLines());
		layout.put("shapes", shapes);

		Map<String, Object> margin = new HashMap<String, Object>();
		margin.put("l", 50);
		margin.put("r", 0);
		margin.put("b", 50);
		margin.put("t", 0);
		margin.put("pad", 4);
		margin.put("autoexpand", false);
		layout.put("margin", margin);

		return layout;
	}

	public void setAnnotation(AnnotationType annotation) {
		this.annotation = annotation;
	}

	public int getHeight() {
		return HEIGHT;
	}

}
