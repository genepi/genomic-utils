package genepi.genomic.utils.commands.gwas.binner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chromosome {

	private static Map<String, Integer> CHROM_ORDERS = new HashMap<>();

	private static final List<Integer> CHROM_LENGTH = Arrays.asList(249250622, 243199374, 198022431, 191154277,
			180915261, 171115068, 159138664, 146364023, 141213432, 135534748, 135006517, 133851896, 115169879,
			107349541, 102531393, 90354754, 81195211, 78077249, 59128984, 63025521, 48129896, 51304567, 0, 155270561,
			59373567, 16570);

	static {
		for (int i = 1; i <= 22 + 1; i++) {
			CHROM_ORDERS.put(i + "", i);
		}
		CHROM_ORDERS.put("X", CHROM_ORDERS.size());
		CHROM_ORDERS.put("Y", CHROM_ORDERS.size());
		CHROM_ORDERS.put("MT", CHROM_ORDERS.size());
	}

	public static int getOrder(String chrom) {
		return CHROM_ORDERS.get(chrom);
	}

	public static long getLength(String chrom) {
		return getLength(getOrder(chrom));
	}

	public static long getLength(int chrom) {
		return CHROM_LENGTH.get(chrom);
	}

}
