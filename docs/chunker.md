# chunker

This tool chunks VCF files by their region or by their number of variants.

## Basic Usage
### Without VcfWriter
- **RegionChunker:** 

Use dataSmall.vcf as input file, set output file to MANIFEST.txt, set chunksize to 1000 
(each time position is over 1000 a new chunk is made) and use strategy CHUNK_BY_REGION to chunk by the position
```
java -jar genomic-utils.jar file-chunking \
dataSmall.vcf \
--output MANIFEST.txt --chunksize 1000 --strategy CHUNK_BY_REGION 
```
output in this example -> `MANIFEST.txt` with 3 Lines (header included)

- **VariantChunker:**

Use dataSmall.vcf as input file, set output file to MANIFEST.txt, set chunksize to 4
(every 4 variants a new chunk is made) and use strategy CHUNK_BY_VARIANTS to chunk by the number of variants
```
java -jar genomic-utils.jar file-chunking \
dataSmall.vcf  \
--output MANIFEST.txt --chunksize 4 --strategy CHUNK_BY_VARIANTS 
```
output in this example -> `MANIFEST.txt` with 3 Lines (header included)

### With VcfWriter
- **RegionChunker:**

Use dataSmall.vcf as input file, set output file to MANIFEST.txt, set chunksize to 1000
(each time position is over 1000 a new chunk is made), use strategy CHUNK_BY_REGION to chunk by the number of variants
and set the outputdirectory to "test-data/chunker" so the vcfwriter will be used and write each chunk and the MANIFEST.txt to this directory 
```
java -jar genomic-utils.jar file-chunking \
dataSmall.vcf  \
--output MANIFEST.txt --chunksize 1000 --strategy CHUNK_BY_REGION \
--output-dir "test-data/chunker"
```
output in this example -> `test-data/chunker/MANIFEST.txt`; `test-data/chunker/chunk20,0,1000.vcf` and `test-data/chunker/chunk20,1001,2000.vcf`


- **VariantChunker:**
 
Use dataSmall.vcf as input file, set output file to MANIFEST.txt, set chunksize to 4
(every 4 variants a new chunk is made), use strategy CHUNK_BY_VARIANTS to chunk by the number of variants
and set the outputdirectory to "test-data/chunker" so the vcfwriter will be used and write each chunk and the MANIFEST.txt to this directory

```
java -jar genomic-utils.jar file-chunking \
dataSmall.vcf \
--output MANIFEST.txt --chunksize 4 --strategy CHUNK_BY_VARIANTS
--output-dir "test-data/chunker" 
```
output in this example -> `test-data/chunker/MANIFEST.txt`; `test-data/chunker/chunk20,0,4.vcf` and `test-data/chunker/chunk20,5,8.vcf`