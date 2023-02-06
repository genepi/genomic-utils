# tabix-merge

![build](https://github.com/lukfor/magic-progress/workflows/build/badge.svg)
[ ![Download](https://api.bintray.com/packages/lukfor/maven/magic-progress/images/download.svg) ](https://bintray.com/lukfor/maven/magic-progress/_latestVersion)

> Merging two tabi delimitted files with tabix indices.


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
java -jar tabix-merge.jar annotate \
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

## License

`tabix-merge` is MIT Licensed.
