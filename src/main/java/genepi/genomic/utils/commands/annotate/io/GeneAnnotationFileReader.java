package genepi.genomic.utils.commands.annotate.io;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import genepi.io.table.reader.CsvTableReader;
import htsjdk.samtools.util.IntervalTree;
import htsjdk.samtools.util.IntervalTree.Node;

public class GeneAnnotationFileReader {

	private static final String DISTANCE = "GENE_DISTANCE";
	private Map<String, IntervalTree<Gene>> intervalTrees;

	public GeneAnnotationFileReader(String filename, String[] annotationColumns, char annoSparator,
			boolean annoComments, String annoChr, String annoStart, String annoEnd) {

		intervalTrees = new HashMap<String, IntervalTree<Gene>>();

		CsvTableReader reader = new CsvTableReader(filename, annoSparator);
		while (reader.next()) {

			Gene gene = new Gene();
			String chromsome = reader.getString(annoChr);
			int start = reader.getInteger(annoStart);
			int stop = reader.getInteger(annoEnd);

			IntervalTree<Gene> intervalTree = getByChromosome(chromsome);
			for (String column : annotationColumns) {
				if (!column.equals(DISTANCE)) {
					gene.put(column, reader.getString(column));
				}
			}
			intervalTree.put(start, stop, gene);

		}
		reader.close();

	}

	public IntervalTree<Gene> getByChromosome(String chromosome) {
		IntervalTree<Gene> intervalTree = intervalTrees.get(chromosome);
		if (intervalTree == null) {
			intervalTree = new IntervalTree<Gene>();
			intervalTrees.put(chromosome, intervalTree);
		}

		return intervalTree;
	}

	public List<Gene> query(String chromosome, int position, int window) {
		List<Gene> result = new Vector<Gene>();
		IntervalTree<Gene> intervalTree = intervalTrees.get(chromosome);
		if (intervalTree == null) {
			return result;
		}

		// overlapping genes
		Iterator<Node<Gene>> overlappers = intervalTree.overlappers(position, position);
		if (overlappers.hasNext()) {
			do {
				Gene gene = new Gene(overlappers.next().getValue());
				gene.put(DISTANCE, "0");
				result.add(gene);
			} while (overlappers.hasNext());
			return result;
		}

		// nearest genes
		Node<Gene> min = intervalTree.min(position, position);
		Node<Gene> max = intervalTree.max(position, position);
		if (min != null && max != null) {
			int minDistance = distance(position, min);
			int maxDistance = distance(position, max);
			if (minDistance < maxDistance) {
				Gene gene = new Gene(min.getValue());
				gene.put(DISTANCE, minDistance + "");
				result.add(gene);
			} else {
				Gene gene = new Gene(max.getValue());
				gene.put(DISTANCE, maxDistance + "");
				result.add(gene);
			}
		} else if (min != null) {
			int minDistance = distance(position, min);
			Gene gene = new Gene(min.getValue());
			gene.put(DISTANCE, minDistance + "");
			result.add(gene);
		} else if (max != null) {
			int maxDistance = distance(position, max);
			Gene gene = new Gene(max.getValue());
			gene.put(DISTANCE, maxDistance + "");
			result.add(gene);
		}

		return result;

	}

	private int distance(int position, Node<Gene> node) {
		if (node.getEnd() < position) {
			// after
			return position - node.getEnd();
		} else if (node.getStart() > position) {
			// before
			return node.getStart() - position;
		} else {
			// intersection
			return 0;
		}
	}

	public static class Gene extends HashMap<String, String> {

		public Gene() {
			super();
		}
		
		public Gene(Gene value) {
			this();
			putAll(value);
		}

		private static final long serialVersionUID = 1L;

	}

}
