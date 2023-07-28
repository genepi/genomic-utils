package genepi.genomic.utils.commands.bgen;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import genepi.genomic.utils.commands.bgen.io.BgenIndexFileReader;
import genepi.genomic.utils.commands.bgen.io.FastVCFFileReader;
import genepi.genomic.utils.commands.bgen.io.Variant;
import genepi.genomic.utils.commands.bgen.io.IVariantReader;
import genepi.genomic.utils.commands.bgen.util.ChunkingStrategy;
import genepi.io.table.writer.CsvTableWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command
public class BgenChunkCommand implements Callable<Integer> {

	@Parameters(description = "bgen files")
	List<String> inputs;

	@Option(names = { "--size" }, description = "Chunk Size", required = true, showDefaultValue = Visibility.ALWAYS)
	int chunkSize = 0;

	@Option(names = {
			"--out" }, description = "Output Chunk Filename", required = true, showDefaultValue = Visibility.ALWAYS)
	String output = "";

	@Option(names = {
			"strategy" }, description = "Chunking Strategy: RANGE or VARIANTS. Default: RANGE", required = false, showDefaultValue = Visibility.ALWAYS)
	ChunkingStrategy strategy = ChunkingStrategy.RANGE;

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void setStrategy(ChunkingStrategy strategy) {
		this.strategy = strategy;
	}

	public Integer call() throws Exception {

		CsvTableWriter writer = new CsvTableWriter(output);
		writer.setColumns(new String[] { "CONTIG", "START", "END", "VARIANTS", "FILENAME" });

		int countChunks = 0;
		for (String filename : inputs) {

			System.out.println("Reading file '" + filename + "'...");

			IVariantReader reader = null;
			if (filename.endsWith(".bgen")) {
				reader = new BgenIndexFileReader(filename + ".bgi");
			} else if (filename.endsWith(".vcf") || filename.endsWith(".vcf.gz")) {
				reader = new FastVCFFileReader(filename);
			} else {
				throw new IOException("Unkonwn file format");
			}
			String contig = null;

			Map<Integer, VcfChunk> chunks = new ConcurrentHashMap<Integer, VcfChunk>();

			int variantNumber = 0;
			VcfChunk lastChunk = null;
			while (reader.next()) {
				Variant variant = reader.get();
				variantNumber++;

				if (contig != null && !variant.getContig().equals(contig)) {
					System.out.println("Error: Multiple contigs in file " + filename + ". Found contigs '" + contig
							+ "' and '" + variant.getContig() + "'");
					return 1;
				}
				contig = variant.getContig();

				switch (strategy) {
				case RANGE:
					lastChunk = calcChankForRange(chunks, variant);
					break;

				case VARIANTS:
					lastChunk = calcChankForVariants(chunks, variant, variantNumber, lastChunk);
					break;
				}

			}
			reader.close();

			for (VcfChunk chunk : chunks.values()) {
				writer.setString("CONTIG", chunk.getContig());
				writer.setInteger("START", chunk.getStart());
				writer.setInteger("END", chunk.getEnd());
				writer.setInteger("VARIANTS", chunk.getVariants());
				writer.setString("FILENAME", filename);
				writer.next();
				countChunks++;
			}
		}

		writer.close();

		System.out.println("Done. Wrote " + countChunks + " chunks to file '" + output + "'.");

		return 0;
	}

	private VcfChunk calcChankForVariants(Map<Integer, VcfChunk> chunks, Variant variant, int variantNumber,
			VcfChunk lastChunk) {

		VcfChunk chunk = null;
		if (lastChunk == null) {
			// first chunk
			chunk = new VcfChunk(variant.getContig(), variant.getStart(), -1);
			chunks.put(chunks.size(), chunk);
		} else if (lastChunk.getVariants() >= chunkSize) {
			// last chunk has NO capacity. Start new chunk.
			chunk = new VcfChunk(variant.getContig(), variant.getStart(), -1);
			chunks.put(chunks.size(), chunk);
		} else {
			// last chunk has capacity
			chunk = lastChunk;
		}
		// extend end position and inc variant counter
		chunk.setEnd(variant.getStart());
		chunk.incVariants();
		return chunk;
	}

	private VcfChunk calcChankForRange(Map<Integer, VcfChunk> chunks, Variant variant) {
		int index = variant.getStart();
		int chunkNumber = index / chunkSize;
		if (index % chunkSize == 0) {
			chunkNumber = chunkNumber - 1;
		}
		index = variant.getStart();
		VcfChunk chunk = chunks.get(chunkNumber);
		if (chunk == null) {
			int chunkStart = chunkNumber * chunkSize + 1;
			int chunkEnd = chunkStart + chunkSize - 1;
			chunk = new VcfChunk(variant.getContig(), chunkStart, chunkEnd);
			chunks.put(chunkNumber, chunk);
		}

		chunk.incVariants();
		return chunk;
	}

	class VcfChunk {

		private String contig;

		private int start;

		private int end;

		private int variants = 0;

		public VcfChunk(String contig, int start, int end) {
			super();
			this.contig = contig;
			this.start = start;
			this.end = end;
		}

		public String getContig() {
			return contig;
		}

		public void setContig(String contig) {
			this.contig = contig;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public int getVariants() {
			return variants;
		}

		public void setVariants(int variants) {
			this.variants = variants;
		}

		public void incVariants() {
			this.variants++;
		}

	}

}
