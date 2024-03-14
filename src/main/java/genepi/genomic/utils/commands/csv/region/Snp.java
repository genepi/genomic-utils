package genepi.genomic.utils.commands.csv.region;

public class Snp {

	private String chromosome;

	private long position;

	private String reference;

	private String alternate;

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReference() {
		return reference;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getAlternate() {
		return alternate;
	}

	@Override
	public String toString() {
		return chromosome + ":" + position + ":" + reference;
	}

}
