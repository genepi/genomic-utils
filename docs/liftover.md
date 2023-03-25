# liftover

This tool lifts genomic positions between genome builds. Currently, builds hg19 and hg38 are supported.   

## Chain files
- hg19 -> hg38: `files/chain/hg19ToHg38.over.chain.gz`

- hg38 -> hg19: `files/chain/hg38ToHg19.over.chain.gz`

## Lift from build hg19 to hg38

This command lifts an input file (`regenie.txt.gz`) to build hg38 and updates the ID column to `chr:pos:REF:ALT`.

```
java -jar /opt/genomic-utils.jar liftover \
--position GENPOS --ref ALLELE0 --alt ALLELE1 --chr CHROM --id ID --update-id \
--chain files/chain/hg19ToHg38.over.chain.gz \
--input regenie.txt.gz --output regenie_hg38.txt.gz
```
