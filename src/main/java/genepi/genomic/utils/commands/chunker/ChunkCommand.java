package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.chunkers.VariantChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import genepi.genomic.utils.commands.chunker.vcf.VcfWriter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class ChunkCommand implements Callable<Integer> {

    @Parameters(description = "Input files")
    List<String> inputs;

    @Option(names = "--output", description = "output file", required = true)
    private String output;

    @Option(names = "--chunksize", description = "chunksize", required = true)
    private int chunksize;

    @Option(names = "--strategy", description = "chunker strategies:  ${COMPLETION-CANDIDATES}")
    private ChunkingStrategy strategy = ChunkingStrategy.CHUNK_BY_REGION;

    @Option(names = "--output-dir", description = "output directory")
    private String outputDirectory = "";

    @Option(names = "--output-format", description = "output formats: ${COMPLETION-CANDIDATES}")
    private ChunkingOutput chunkingOutput = ChunkingOutput.VCF;

    private int numberChunks;

    public void setInput(List<String> inputs) {
        this.inputs = inputs;
    }

    public void setChunksize(int chunksize) {
        this.chunksize = chunksize;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setStrategy(ChunkingStrategy strategy) {
        this.strategy = strategy;
    }

    public int getNumberChunks() {
        return this.numberChunks;
    }

    public void setNumberChunks(int numberChunks) {
        this.numberChunks = numberChunks;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public Integer call() throws Exception {

        if (inputs == null || inputs.isEmpty()) {
            System.out.println();
            System.out.println("Please provide at least one input file.");
            System.out.println();
            return 1;
        }
        assert (output != null);
        assert (chunksize > 0);
        assert (chunkingOutput != null);

        IChunker chunker;

        switch (strategy) {
            case CHUNK_BY_REGION:
                chunker = new RegionChunker();
                break;
            case CHUNK_BY_VARIANTS:
                chunker = new VariantChunker();
                break;
            default:
                System.out.println("Strategy not existing");
                return 1;
        }

        IManifestWriter manifestWriter;

        if (!outputDirectory.equals("")) {
            VcfWriter.setOutputDir(outputDirectory);
            chunker.setWriterClass(VcfWriter.class);
            manifestWriter = new ManifestWriter(outputDirectory + "/" + output);
        } else {
            manifestWriter = new ManifestWriter(output);
        }

        chunker.setSize(chunksize);
        for (String input : inputs) {
            try {
                chunker.setReader(new VcfReader(new File(input)));
                chunker.executes();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                return 1;
            }
        }
        setNumberChunks(chunker.getChunks().size());

        manifestWriter.setVcfChunks(chunker.getChunks());
        manifestWriter.write();
        return 0;
    }
}
