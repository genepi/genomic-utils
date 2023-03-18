package genepi.genomic.utils.commands.gwas.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import genepi.genomic.utils.commands.gwas.binner.Variant;

public class MaxPriorityQueue extends PriorityQueue<Variant> {

	public MaxPriorityQueue(int initialCapacity) {

		super(initialCapacity, new Comparator<Variant>() {
			@Override
			public int compare(Variant o1, Variant o2) {
				return Double.compare(o1.pval, o2.pval);
			}
		});
	}

	public void add_and_keep_size(Variant variant, int size, IPoppedCallback popped_callback) {
		if (size() < size) {
			add(variant);
		} else {

			Variant head = peek();
			if (variant.pval > head.pval) {
				// removes head
				poll();
				// add new variant
				add(variant);
				variant = head;
			}
			if (popped_callback != null) {
				popped_callback.call(variant);
			}

		}

	}

}
