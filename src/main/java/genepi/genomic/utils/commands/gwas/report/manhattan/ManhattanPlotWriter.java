package genepi.genomic.utils.commands.gwas.report.manhattan;

import java.io.File;
import java.util.List;
import java.util.Vector;

import genepi.genomic.utils.commands.gwas.binner.Bin;
import genepi.genomic.utils.commands.gwas.binner.ChromBin;
import genepi.genomic.utils.commands.gwas.binner.Variant;
import genepi.genomic.utils.commands.gwas.util.BinningAlgorithm;
import genepi.io.table.writer.CsvTableWriter;
import genepi.io.table.writer.ITableWriter;

public class ManhattanPlotWriter {

	private String chr = "CHROM";

	private String position = "GENPOS";

	private String pval = "LOG10P";

	private char separator = '\t';

	private String ref = "ALLELE0";

	private String alt = "ALLELE1";

	private String beta = "BETA";

	private String rsid = "ID";

	private String gene = null;

	private ManhattanPlot data;

	private BinningAlgorithm binningAlgorithm = BinningAlgorithm.BIN_TO_POINTS_AND_LINES;

	public ManhattanPlotWriter(ManhattanPlot data) {
		this.data = data;
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

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setBeta(String beta) {
		this.beta = beta;
	}

	public void setRsid(String rsid) {
		this.rsid = rsid;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public void setBinningAlgorithm(BinningAlgorithm binningAlgorithm) {
		this.binningAlgorithm = binningAlgorithm;
	}
	
	public void saveAsFile(File file) {

		List<String> columns = new Vector<String>();
		columns.add(chr);
		columns.add(position);
		columns.add(pval);
		if (ref != null) {
			columns.add(ref);
		}
		if (alt != null) {
			columns.add(alt);
		}
		if (beta != null) {
			columns.add(beta);
		}
		if (rsid != null) {
			columns.add(rsid);
		}
		if (gene != null) {
			columns.add(gene);
		}
		columns.add("type");
		columns.add("y1");
		columns.add("y2");

		CsvTableWriter writer = new CsvTableWriter(file.getAbsolutePath(), separator, false);

		String[] columnsArray = new String[columns.size()];
		columns.toArray(columnsArray);
		writer.setColumns(columnsArray);

		int unbinned = 0;
		int binned = 0;
		int peak = 0;

		int bins = 0;

		for (ChromBin chromBin : data.getBins().values()) {

			for (Variant variant : chromBin.getUnbinnedVariants()) {
				writeVariant(writer, variant);
				writer.setString("type", "variant");
				writer.next();
				unbinned++;
			}

			for (Variant variant : chromBin.getPeakVariants()) {
				writeVariant(writer, variant);
				writer.setString("type", "peak");
				writer.next();
				peak++;
			}

			for (Bin bin : chromBin.getBins().values()) {
				bins++;
				for (Double[] line : bin.getLines(binningAlgorithm)) {
					writer.setString(chr, chromBin.chrom);
					writer.setInteger(position, (int) bin.startpos);
					if (line[0].equals(line[1])) {
						writer.setString("type", "bin_point");
						writer.setDouble("y1", line[0]);
					} else {
						writer.setString("type", "bin_line");
						writer.setDouble("y1", line[0]);
						writer.setDouble("y2", line[1]);
					}
					writer.next();
					binned++;
				}
			}
		}

		writer.close();

		System.out.println("Wrote to file " + file.getAbsolutePath() + " Unbinned: " + unbinned + ". Peaks: " + peak
				+ ". Binned: " + binned + " in " + bins + " bins");

	}

	protected void writeVariant(ITableWriter writer, Variant variant) {
		writer.setString(chr, variant.chrom);
		writer.setInteger(position, (int) variant.pos);
		writer.setDouble(pval, variant.pval);
		if (ref != null) {
			writer.setString(ref, variant.ref);

		}
		if (alt != null) {
			writer.setString(alt, variant.alt);
		}
		if (beta != null) {
			writer.setString(beta, variant.beta);
		}
		if (rsid != null) {
			writer.setString(rsid, variant.id);
		}
		if (gene != null) {
			writer.setString(gene, variant.gene);
		}
	}

}
