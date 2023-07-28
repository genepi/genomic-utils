package genepi.genomic.utils.commands.bgen.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import genepi.io.FileUtil;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class FastVCFFileReader implements IVariantReader {

	private List<String> samples;

	private int snpsCount = 0;

	private int samplesCount = 0;

	private Variant variant = new Variant();

	private String filename;

	protected BufferedReader in;

	private int lineNumber = 0;

	public FastVCFFileReader(InputStream inputStream, String vcfFilename) throws IOException {

		// load header
		VCFFileReader reader = new VCFFileReader(new File(vcfFilename), false);
		VCFHeader header = reader.getFileHeader();
		samples = header.getGenotypeSamples();
		samplesCount = samples.size();
		reader.close();

		filename = vcfFilename;
		InputStream in2 = FileUtil.decompressStream(inputStream);
		in = new BufferedReader(new InputStreamReader(in2));

	}

	public FastVCFFileReader(String vcfFilename) throws IOException {
		this(new FileInputStream(vcfFilename), vcfFilename);
	}

	public List<String> getGenotypedSamples() {
		return samples;
	}

	@Override
	public Variant get() {
		return variant;
	}

	public int getSnpsCount() {
		return snpsCount;
	}

	public int getSamplesCount() {
		return samplesCount;
	}

	@Override
	public boolean next() throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			try {
				lineNumber++;
				if (!line.trim().isEmpty() && line.charAt(0) != '#') {
					parseLine(line);
					return true;
				}
			} catch (Exception e) {
				throw new IOException(filename + ": Line " + lineNumber + ": " + e.getMessage());
			}
		}
		return false;
	}

	protected void parseLine(String line) throws IOException {

		String tiles[] = line.split("\t", 3);

		if (tiles.length < 3) {
			throw new IOException("The provided VCF file is not correct tab-delimited");
		}

		variant.setContig(tiles[0]);
		variant.setStart(Integer.parseInt(tiles[1]));

		snpsCount++;

	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}