package genepi.genomic.utils.commands.chunker;

import genepi.genomic.utils.commands.chunker.chunkers.RegionChunker;
import genepi.genomic.utils.commands.chunker.chunkers.VariantChunker;
import genepi.genomic.utils.commands.chunker.vcf.VcfReader;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class VcfChunkerCommand implements Callable<Integer> {

    @Option(names = "--input", description = "input vcf file", required = true)
    private List<String> input;

    @Option(names = "--output", description = "output file", required = true)
    private String output;

    @Option(names = "--chunksize", description = "chunksize", required = true)
    private int chunksize;

    @Option(names ="--chunk-by-size", description = "chunker type", required = true)
    private boolean chunkBySize = false;

    @Option(names ="--chunk-by-variant", description = "chunker type", required = true)
    private boolean chunkByVariant = false;

    public void setInput(List<String> input) {
        this.input = input;
    }

    public void setChunksize(int chunksize) {
        this.chunksize = chunksize;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setChunkBySize(boolean chunkBySize) {
        this.chunkBySize = chunkBySize;
    }

    @Override
    public Integer call() throws Exception {
        assert (input != null && input.size() > 0);
        assert (output != null);
        assert (chunksize > 0);

        IChunker chunker = null;

        if(chunkBySize) {
            chunker = new RegionChunker();
        } else if(chunkByVariant) {
            chunker = new VariantChunker();
        } else {
            //TODO: throw exception if none is selected --> otherwise chunker is null
        }

        int lines = 0;

        for (String input : input){
            chunker.setSize(chunksize);
            chunker.setReader(new VcfReader(new File(input)));
            chunker.executes();
            lines += chunker.getChunks().size();
        }
        chunker.setManifestWriter(new ManifestWriter(output));
        return chunker.getChunks().size();
    }
}
