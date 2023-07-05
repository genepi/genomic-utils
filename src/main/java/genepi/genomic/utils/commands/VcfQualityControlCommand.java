package genepi.genomic.utils.commands;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import genepi.io.text.LineWriter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "vcf-quality-control", version = App.VERSION)
public class VcfQualityControlCommand implements Callable<Integer> {

	@Parameters(description = "vcf files")
	List<String> vcfFilenames;

	@Option(names = "--output", description = "output prefix", required = true)
	private String outputPrefix;

	@Option(names = "--minSnpCallRate", description = "minSnpCallRate", required = false, showDefaultValue = Visibility.ALWAYS)
	private double minSnpCallRate = 0.9;

	@Option(names = "--minSampleCallRate", description = "minSampleCallRate", required = false, showDefaultValue = Visibility.ALWAYS)
	private double minSampleCallRate = 0.5;

	@Option(names = "--chunkSize", description = "Calculate sample call rate for chunks of this size. No chunk size: 0", required = false, showDefaultValue = Visibility.ALWAYS)
	private int chunkSize = 0;

	public void setVcfFilenames(List<String> vcfFilenames) {
		this.vcfFilenames = vcfFilenames;
	}

	public void setVcfFilenames(String... vcfFilenames) {
		this.vcfFilenames = new Vector<String>();
		for (String filename : vcfFilenames) {
			this.vcfFilenames.add(filename);
		}
	}

	public void setOutput(String outputPrefix) {
		this.outputPrefix = outputPrefix;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	@Override
	public Integer call() throws Exception {

		assert (vcfFilenames != null);
		assert (vcfFilenames.size() > 0);
		assert (outputPrefix != null);

		int totalSnps = 0;
		int includedSnps = 0;
		int lowSnpCallRate = 0;

		List<String> samples = null;
		int[] snpsPerSampleCount = null;
		Chunks chunks = null;

		LineWriter writerExcludedSnps = new LineWriter(outputPrefix + ".snps.excluded");
		LineWriter writerSnps = new LineWriter(outputPrefix + ".snps");
		writerSnps.write("snp call_rate samples missings");
		for (String filename : vcfFilenames) {

			System.out.println("Process file '" + filename + "'...");

			VCFFileReader reader = new VCFFileReader(new File(filename), false);

			List<String> currentSamples = reader.getFileHeader().getGenotypeSamples();
			if (samples == null) {
				samples = currentSamples;
				snpsPerSampleCount = new int[samples.size()];
				for (int i = 0; i < samples.size(); i++) {
					snpsPerSampleCount[i] = 0;
				}
				if (chunkSize > 0) {
					chunks = new Chunks(samples.size(), chunkSize);
				}
			} else {
				if (!samples.equals(currentSamples)) {
					System.out.println("Error! Different samples.");
					reader.close();
					return 1;
				}
			}

			for (VariantContext snp : reader) {

				double snpCallRate = 1.0 - (snp.getNoCallCount() / (double) snp.getNSamples());
				if (snpCallRate < minSnpCallRate) {
					lowSnpCallRate++;
					writerExcludedSnps.write(snp.getID());
				} else {

					for (int i = 0; i < snp.getNSamples(); i++) {
						if (snp.getGenotype(i).isCalled()) {
							// calculate sample call rate for whole genome
							snpsPerSampleCount[i] += 1;
							if (chunks != null) {
								// calculate sample call rate for chunks
								chunks.getBySnp(snp).incSample(i);
							}
						}
					}

					includedSnps++;

					if (chunks != null) {
						chunks.getBySnp(snp).incSnps();
					}

				}
				writerSnps
						.write(snp.getID() + " " + snpCallRate + " " + snp.getNSamples() + " " + snp.getNoCallCount());
				totalSnps++;
			}

			reader.close();
		}
		writerSnps.close();
		writerExcludedSnps.close();

		LineWriter writerSamples = new LineWriter(outputPrefix + ".samples");
		String header = "sample chunk call_rate snps missings";
		writerSamples.write(header);

		List<String> excludedSamples = new Vector<String>();
		int lowSampleCallRate = 0;
		int lowSampleCallRateInChunk = 0;
		for (int i = 0; i < samples.size(); i++) {

			boolean excluded = false;

			// calculate sample call rate for whole genome
			int snps = snpsPerSampleCount[i];
			double sampleCallRate = snps / (double) includedSnps;
			if (sampleCallRate < minSampleCallRate) {
				excludedSamples.add(samples.get(i));
				lowSampleCallRate++;
				excluded = true;
			}
			writerSamples.write(samples.get(i) + " whole_genome " + sampleCallRate + " " + includedSnps + " "
					+ (includedSnps - snps));

			// calculate sample call rate for chunks
			if (chunks != null) {
				for (String chunk : chunks.getChunkNames()) {
					int calledSnpsInChunk = chunks.getByName(chunk).getSample(i);
					int totalSnpsInChunk = chunks.getByName(chunk).getSnps();
					double sampleCallRateInChunk = calledSnpsInChunk / (double) totalSnpsInChunk;
					if (sampleCallRateInChunk < minSampleCallRate) {
						if (!excluded) {
							excludedSamples.add(samples.get(i));
							lowSampleCallRateInChunk++;
							excluded = true;
						}
					}
					writerSamples.write(samples.get(i) + " " + chunk + " " + sampleCallRateInChunk + " "
							+ totalSnpsInChunk + " " + (totalSnpsInChunk - calledSnpsInChunk));
				}
			}
		}
		writerSamples.close();

		// write all excluded samples to file
		LineWriter writerExcludedSamples = new LineWriter(outputPrefix + ".samples.excluded");
		for (String sample : excludedSamples) {
			writerExcludedSamples.write(sample);
		}
		writerExcludedSamples.close();

		System.out.println("");
		System.out.println("Files: " + vcfFilenames.size());
		System.out.println("Samples: " + samples.size());
		System.out.println("Snps: " + totalSnps);
		if (chunks != null) {
			System.out.println("Chunks: " + chunks.getChunkNames().size());
		}
		System.out.println("");
		System.out.println("Filtered: ");
		System.out.println("  Samples: ");
		System.out.println("    Call Rate < " + minSampleCallRate + " (whole genome): " + lowSampleCallRate);
		System.out.println("    Call Rate < " + minSampleCallRate + " (in 1 chunk): " + lowSampleCallRateInChunk);
		System.out.println("  Snps: ");
		System.out.println("    Call Rate < " + minSnpCallRate + ": " + lowSnpCallRate);
		System.out.println("");
		return 0;
	}

	class Chunks {

		private int samples;

		private int chunkSize;

		private List<String> chunks = new Vector<String>();

		private Map<String, Chunk> storage = new HashMap<String, Chunk>();

		public Chunks(int samples, int chunkSize) {
			this.samples = samples;
			this.chunkSize = chunkSize;
		}

		public Chunk getBySnp(VariantContext snp) {
			int chunkNumber = (snp.getStart() / chunkSize) + 1;
			String chunkName = "chr" + snp.getContig() + "_" + chunkNumber;
			Chunk chunk = storage.get(chunkName);
			if (chunk == null) {
				chunk = new Chunk(samples);
				storage.put(chunkName, chunk);
				chunks.add(chunkName);
			}

			return chunk;
		}

		public Chunk getByName(String name) {
			return storage.get(name);
		}

		public List<String> getChunkNames() {
			return chunks;
		}
	}

	class Chunk {

		private int snps = 0;

		private int[] samples;

		public Chunk(int sampleSize) {
			samples = new int[sampleSize];
			for (int i = 0; i < sampleSize; i++) {
				samples[i] = 0;
			}
		}

		public int getSample(int sample) {
			return samples[sample];
		}

		public int incSample(int sample) {
			return samples[sample]++;
		}

		public int getSnps() {
			return snps;
		}

		public void incSnps() {
			snps++;
		}

	}

}