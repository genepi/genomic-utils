# annotate

Merge csv file with with tabix indexed file. This command could be used to annotate variants.


## Index files

### Downloads a csv file from pheweb resources

Mapping files from pheweb are available on: https://resources.pheweb.org/

```
wget https://resources.pheweb.org/rsids-v154-hg19.tsv.gz
```

Add header, convert to bgzip format and create index on `CHROM` and `POS`:

```
echo "CHROM\tPOS\tRSID\tREF\tALT" | bgzip -c > rsids-v154-hg19.index.gz
gzcat rsids-v154-hg19.tsv.gz | bgzip -c >> rsids-v154-hg19.index.gz
tabix -s1 -b2 -e2 -S1 rsids-v154-hg19.index.gz
```

*Note: Replace `gzcat`with `zcat`on linux.*


## Basic usage

### Annotate regenie file with rsID

Annotate file `regenie.txt.gz` with rsID from `rsids-v154-hg19.index.gz` and store output in `regenie.annotated.txt`:

```
java -jar genomic-utils.jar annotate \
  --input regenie.txt.gz \
  --chr CHROM \
  --pos GENPOS \
  --ref ALLELE0 \
  --alt ALLELE1 \
  --anno rsids-v154-hg19.index.gz \
  --anno-columns RSID \
  --strategy CHROM_POS_ALLELES \
  --output regenie.annotated.txt
```
