# Lucene Based Productvariantsearch

returning "products" while searching for "productvariants" is just pain using elasticsearch right now.
the main problem is that elasticsearch doesn't support lucene's ToParentBlockJoinQuery.

This makes it impossible to return matched "productvariants" grouped by a "product".
workarounds like fetching the source and find the matched variants in the application or run a subquery for every matched
product may isn't suitable.

this is a POC for building a productsearch with productvariants using lucene.

take a look at the Tests (ns de.tg.productsearch.Test) if you want to learn about the api.


## Features

for now it's just a POC. I implemented a bunch of features:

[xx] search for products and productvariants and return the products SKU

[xx] return matched productvariants

[xx] create product facets for productvariants (correct counting for products).

[x ] update Products in realtime.

[  ] examples for complex queries

[  ] write a blog post about this topic

[  ] support stemming

[  ] support dynamic facets

[  ] add example for range facets

[  ] support for analyzer

[  ] rest api

[  ] performance tests

[  ] persistent storage (right now just in memory)

[  ] mongodb river


---
[xx] = implemented + integrationtest

[x ] = implemented

[  ] = todo



