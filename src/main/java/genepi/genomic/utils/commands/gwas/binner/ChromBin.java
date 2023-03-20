package genepi.genomic.utils.commands.gwas.binner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ChromBin {

	public ChromBin(String chrom) {
		this.chrom = chrom;
	}

	public String chrom;

	private Map<Long, Bin> bins = new HashMap<>();

	private long length = 0;;

	private List<Variant> unbinnedVariants = new Vector<Variant>();;

	private List<Variant> peakVariants = new Vector<>();

	public List<Variant> getPeakVariants() {
		return peakVariants;
	}

	public List<Variant> getUnbinnedVariants() {
		return unbinnedVariants;
	}

	public String getChrom() {
		return chrom;
	}

	public Map<Long, Bin> getBins() {
		return bins;
	}

	public void addPeakVariant(Variant variant) {
		assert (variant.chrom.equals(chrom));
		if (variant.pos > length) {
			length = variant.pos;
		}
		peakVariants.add(variant);
	}

	public void addUnbinnedVariant(Variant variant) {
		assert (variant.chrom.equals(chrom));
		if (variant.pos > length) {
			length = variant.pos;
		}
		unbinnedVariants.add(variant);
	}
	
	public long getLength() {
		return length;
	}

	public void addBin(long pos_bin_id, Bin bin) {
		if (bin.startpos > length) {
			length = bin.startpos;
		}
		bins.put(pos_bin_id, bin);
	}
	
}
