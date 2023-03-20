package genepi.genomic.utils.commands.gwas.binner;

public class Variant {

	public String id;

	public String gene;

	public double pval;

	public String chrom;

	public long pos;

	public int num_significant_in_peak;

	public String ref;

	public String alt;

	public String beta;

	@Override
	public String toString() {
		return chrom + ":" + pos + " --> " + pval;
	}

	public String getDetails() {
		String label = "";
		if (id != null) {
			label += "SNP: " + id + "<br>";
		}
		label += "Position: " + pos;
		if (gene != null) {
			label += "<br>Gene: " + gene;
		}
		if (ref != null) {
			label += "<br>Ref. Allele: " + ref;
		}
		if (alt != null) {
			label += "<br>Effect Allele: " + alt;
		}
		label += "<br>-log<sub>10</sub>(<i>P</i>): " + pval;
		if (beta != null) {
			label += "<br>Beta: " + beta;
		}
		return label;
	}

	public String getName() {
		if (id != null) {
			return id;
		} else {
			return chrom + ":" + pos;
		}
	}

}
