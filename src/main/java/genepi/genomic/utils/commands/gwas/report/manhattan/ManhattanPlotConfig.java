package genepi.genomic.utils.commands.gwas.report.manhattan;

public class ManhattanPlotConfig {

	public static final double SUGGESTIVE_SIGNIFICANCE_LINE = -Math.log10(1e-5);

	public static final String SUGGESTIVE_SIGNIFICANCE_LINE_COLOR = "#d0d0d0";

	public static final int SUGGESTIVE_SIGNIFICANCE_LINE_WIDTH = 2;

	public static final String SUGGESTIVE_SIGNIFICANCE_LINE_STYLE = "dash";

	public static final double GENOMEWIDE_SIGNIFICANCE_LINE = -Math.log10(5e-08);

	public static final String GENOMEWIDE_SIGNIFICANCE_LINE_COLOR = "#c86467";

	public static final int GENOMEWIDE_SIGNIFICANCE_LINE_WIDTH = 2;

	public static final String GENOMEWIDE_SIGNIFICANCE_LINE_STYLE = "dash";

	public static final String[] CHROMOSOME_COLORS = new String[] { "#779ECB", "#03254c" };

	public static final double BIN_SIZE = 3_000_000;

	public static final int CHROMOSOME_GAP = 10;

	public static final int POINT_SIZE = 5;

}
