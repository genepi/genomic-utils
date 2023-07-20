package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.chunkers.VariantChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class VcfChunkerCommand implements Callable<Integer> {

    @Parameters(description = "Input files")
    List<String> inputs;

    @Option(names = "--output", description = "output file", required = true)
    private String output;

    @Option(names = "--chunksize", description = "chunksize", required = true)
    private int chunksize;

    @Option(names ="--strategy", description = "chunker strategies:  ${COMPLETION-CANDIDATES}", required = false)
    private ChunkingStrategy strategy = ChunkingStrategy.CHUNK_BY_REGION;

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

    public int getNumberChunks(){
        return this.numberChunks;
    }

    public void setNumberChunks(int numberChunks) {
        this.numberChunks = numberChunks;
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

        IChunker chunker;

        if(strategy.equals(ChunkingStrategy.CHUNK_BY_REGION)) {
            chunker = new RegionChunker();
        } else if(strategy.equals(ChunkingStrategy.CHUNK_BY_VARIANTS)) {
            chunker = new VariantChunker();
        } else {
            System.out.println("Strategy not existing");
            return 1;
        }

        for (String input : inputs){
            chunker.setSize(chunksize);
            chunker.setReader(new VcfReader(new File(input)));
            chunker.executes();
        }
        setNumberChunks(chunker.getNumberChunks());
        chunker.setManifestWriter(new ManifestWriter(output));
        chunker.getManifestWriter().setVcfChunks(chunker.getChunks()); //Is not sure if write call will come here and in this form
        chunker.getManifestWriter().write();
        return 0;
    }
    public static void main(String... args) {
        int exitCode = new CommandLine(new VcfChunkerCommand()).execute(args);
        System.exit(exitCode);
    }
}
