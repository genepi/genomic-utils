package genepi.genomic.utils.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import genepi.genomic.utils.App;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(name = "liftover", version = App.VERSION)
public class LiftoverCommand implements Callable<Integer> {

	@Option(names = { "--input" }, description = "Input filename", required = true)
	private String input;

	@Option(names = { "--chr" }, description = "Chromosome column in input file", required = true)
	private String chr;

	@Option(names = { "--position" }, description = "Position column in input file", required = true)
	private String position;

	@Option(names = { "--chain" }, description = "Chain file", required = true)
	private String chainFile;

	@Option(names = {
			"--ref" }, description = "Ref allele column in input file", required = true, showDefaultValue = Visibility.ALWAYS)
	private String ref = null;

	@Option(names = {
			"--alt" }, description = "Alt allele column in input file", required = true, showDefaultValue = Visibility.ALWAYS)
	private String alt = null;

	@Option(names = {
			"--sep" }, description = "Separator of input file", required = false, showDefaultValue = Visibility.ALWAYS)
	private char separator = '\t';

	@Option(names = { "--output" }, description = "Output filename", required = true)
	private String output;

	@Option(names = { "--output-sep" }, description = "Separator of output file", required = false)
	private char outputSep = '\t';

	@Option(names = {
			"--suppress-warnings" }, description = "Suppress warnings", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean suppressWarnings = false;

	@Option(names = {
			"--update-id" }, description = "Activate this flag to update the ID column", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean updateId = false;

	@Option(names = { "--id" }, description = "ID column in input file", required = false)
	private String snpId;

	private int total = 0;

	private int resolved = 0;

	private int failed = 0;

	private int ignored = 0;
	
	private int flipped = 0;

	public static final Map<Character, Character> ALLELE_SWITCHES = new HashMap<Character, Character>();

	static {
		ALLELE_SWITCHES.put('A', 'T');
		ALLELE_SWITCHES.put('T', 'A');
		ALLELE_SWITCHES.put('G', 'C');
		ALLELE_SWITCHES.put('C', 'G');
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setOutputSep(char outputSep) {
		this.outputSep = outputSep;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	public void setUpdateId(boolean updateId) {
		this.updateId = updateId;
	}
	
	public void setSnpId(String snpId) {
		this.snpId = snpId;
	}
	
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public int getTotal() {
		return total;
	}

	public int getFailed() {
		return failed;
	}

	public int getIgnored() {
		return ignored;
	}

	public int getResolved() {
		return resolved;
	}

	public int getFlipped() {
		return flipped;
	}
	
	public void setChainFile(String chainFile) {
		this.chainFile = chainFile;
	}

	public Integer call() throws Exception {

		assert (input != null);
		assert (chr != null);
		assert (position != null);
		assert (output != null);

		info("Load input file from '" + input + "'.");

		CsvTableReader reader = new CsvTableReader(input, separator);

		if (!reader.hasColumn(chr)) {
			error("Column '" + chr + "' not found in file '" + input + "'");
			return 1;
		}

		if (!reader.hasColumn(position)) {
			error("Column '" + position + "' not found in file '" + input + "'");
			return 1;
		}

		if (updateId && snpId == null) {
			error("ID Column not set in file '" + input + "'");
			return 1;
		}

		if (updateId && !reader.hasColumn(snpId)) {
			error("Column '" + snpId + "' not found in file '" + input + "'");
			return 1;
		}

		CsvTableWriter writer = new CsvTableWriter(output, outputSep, false);
		writer.setColumns(reader.getColumns());

		LiftOver liftOver = new LiftOver(new File(chainFile));

		int row = 0;

		try {

			while (reader.next()) {

				row++;

				writer.setRow(reader.getRow());

				String orginalContig = reader.getString(chr);

				boolean ignore = false;

				if (reader.getString(position).isEmpty()) {
					warning("Warning: Row " + row + ": Position is empty. Ignore variant.");
					ignored++;
					ignore = true;
				}
				int originalPosition = 0;
				if (!ignore) {
					try {
						originalPosition = reader.getInteger(position);

					} catch (NumberFormatException e) {
						warning("Warning: Row " + row + ": '" + reader.getString(position)
								+ "' is an invalid position. Ignore variant.");
						ignored++;
						ignore = true;
					}
				}

				String refAllele = reader.getString(ref);
				String altAllele = reader.getString(alt);

				if (!ignore) {

					String contig = "";
					String newContig = "";

					if (orginalContig.equals("chr23")) {
						orginalContig = "chrX";
					}
					if (orginalContig.equals("23")) {
						orginalContig = "X";
					}

					if (orginalContig.startsWith("chr")) {
						contig = orginalContig;
						newContig = orginalContig.replaceAll("chr", "");

					} else {
						contig = "chr" + orginalContig;
						newContig = orginalContig;
					}

					String id = orginalContig + ":" + originalPosition;

					int length = altAllele.length();
					if (length == 0) {
						length = 1;
					}
					int start = originalPosition;
					int stop = originalPosition + length - 1;

					Interval source = new Interval(contig, start, stop, false, id);

					Interval target = liftOver.liftOver(source);

					if (target != null) {

						if (source.getContig().equals(target.getContig())) {

							if (length != target.length()) {

								warning(id + "\t" + "LiftOver" + "\t" + "INDEL_STRADDLES_TWO_INTERVALS. SNP removed.");
								ignore = true;
								failed++;

							} else {

								String reftAlleleNorm = refAllele;
								String altAlleleeNorm = altAllele;

								if (target.isNegativeStrand()) {
									reftAlleleNorm = flip(refAllele);
									altAlleleeNorm = flip(altAllele);
									writer.setString(ref, reftAlleleNorm);
									writer.setString(alt, altAlleleeNorm);
									flipped++;
								}

								writer.setString(chr, newContig);
								writer.setInteger(position, target.getStart());

								if (updateId) {
									writer.setString(snpId, newContig + ":" + target.getStart() + ":" + reftAlleleNorm
											+ ":" + altAlleleeNorm);
								}
								resolved++;

							}

						} else {
							warning(id + "\t" + "LiftOver" + "\t"
									+ "On different chromosome after LiftOver. SNP removed.");
							ignore = true;
							failed++;

						}

					} else {
						warning(id + "\t" + "LiftOver" + "\t" + "LiftOver failed. SNP removed.");
						ignore = true;
						failed++;

					}
				}

				if (!ignore) {
					writer.next();
				}

				total++;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			writer.close();
			reader.close();
		}

		return 0;
	}

	protected static String flip(String allele) {
		String flippedAllele = "";
		for (int i = 0; i < allele.length(); i++) {
			Character flipped = ALLELE_SWITCHES.get(allele.charAt(i));
			flippedAllele += flipped;
		}
		return flippedAllele;
	}

	protected void error(String message) {
		System.out.println("Error: " + message);
	}

	protected void warning(String message) {
		if (!suppressWarnings) {
			System.out.println("Warning: " + message);
		}
	}

	protected void info(String message) {
		System.out.println(message);
	}

	protected void success(String message) {
		System.out.println(message);
	}

}
