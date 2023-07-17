package genepi.genomic.utils.commands.chunker;

import java.io.File;
import java.util.concurrent.Callable;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import picocli.CommandLine.Option;

public class VcfChunkerCommand implements Callable<Integer> {

    @Option(names = "--input", description = "input vcf file", required = true)
    private String input;

    @Option(names = "--output", description = "output file", required = true)
    private String output;

    @Option(names = "--chunksize", description = "chunksize", required = true)
    private int chunksize;

    public void setInput(String input) {
        this.input = input;
    }

    public void setChunksize(int chunksize) {
        this.chunksize = chunksize;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public Integer call() throws Exception {
        assert (input != null);
        assert (output != null);

        RegionChunker chunker = new RegionChunker();

        chunker.setReader(new VcfReader(new File(input)));
        chunker.setManifestWriter(new ManifestWriter(output));

        chunker.executes(chunksize);

        return null;
    }
}
