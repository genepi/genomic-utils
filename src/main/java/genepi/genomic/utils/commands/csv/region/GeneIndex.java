package genepi.genomic.utils.commands.csv.region;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import genepi.io.text.LineReader;

public class GeneIndex {

	private static Map<String, GenomicRegion> genesIndex = new HashMap<String, GenomicRegion>();

	public static void load(String filename, String build) throws IOException {
		LineReader reader = new LineReader(filename);
		while (reader.next()) {
			String line = reader.get();
			String[] tiles = line.split("\t");
			if (tiles.length == 4) {
				GenomicRegion region = new GenomicRegion();
				if (build == "hg38") {
					region.setChromosome("chr" + tiles[0]);
				} else {
					region.setChromosome(tiles[0]);
				}
				region.setStart(Integer.parseInt(tiles[1]));
				region.setEnd(Integer.parseInt(tiles[2]));
				genesIndex.put(tiles[3].toLowerCase(), region);
			}
		}
		reader.close();

		System.out.println("Loaded " + genesIndex.size() + " genes from file " + filename);

	}

	public static GenomicRegion findGene(String gene) {
		return genesIndex.get(gene.toLowerCase());
	}

}
