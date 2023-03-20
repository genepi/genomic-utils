package genepi.genomic.utils.commands.gwas.binner;

public class ChrPosition {

	public ChrPosition(String chrom, long pos) {
		this.chr = chrom;
		this.position = pos;
	}

	public String chr;

	public long position;

}
