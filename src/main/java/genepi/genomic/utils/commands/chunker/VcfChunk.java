package genepi.genomic.utils.commands.chunker;

import java.io.File;

public interface VcfChunk {
     int start = 0;
     int end = 0;
     int variants = 0;
     int samples = 0;
     File file = null;
}
