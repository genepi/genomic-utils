package genepi.genomic.utils.commands.gwas.util;

import genepi.genomic.utils.commands.gwas.binner.Variant;

public interface IPoppedCallback {

	public void call(Variant variant);
	
}
