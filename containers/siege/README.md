# Load testing / benchmarking with Siege

siege - An HTTP/HTTPS stress tester

[Siege man page](http://linux.die.net/man/1/siege)


## Docker image
This Docker image contains a opinionated version of the siege tool, specifically configured for this project.

It adds a pre-configured .siegerc file with the following non-default settings:

	verbose = false
	gmethod = GET
	logging = false
	concurrent = 20
	time = 5S
	benchmark = true
	
These can obviously be changed to something else before building. Just edit the .siegerc file.

## Build

	docker build -t siege-engine .

## Running
### print help text
Default behaviour is to print siege --help:

	docker run --rm siege-engine

### Test request
If you just want to test that a server is up and running, you can use the `siege -g` feature to just request the page once. Normally `siege -g` would get the header only, but in this image it is reconfigured to get the entire page. This makes it easy to verify that the page is actually the correct one, e.g. containing `hello world!` or whatever you expect.

	docker run --rm siege-engine -g http://192.168.99.100:8000/

### Running a default siege 
The added .siegerc file has default settings for putting your webserver under benchmark siege with 20 users for 5 S.:

	docker run --rm siege-engine <url>

e.g. 

	docker run --rm siege-engine http://192.168.99.100:8000/

Print output like the following:

	** SIEGE 3.0.8
	** Preparing 20 concurrent users for battle.
	The server is now under siege...
	Lifting the server siege...      done.

	Transactions:		       14999 hits
	Availability:		      100.00 %
	Elapsed time:		        4.96 secs
	Data transferred:	        0.17 MB
	Response time:		        0.01 secs
	Transaction rate:	     3023.99 trans/sec
	Throughput:		        0.03 MB/sec
	Concurrency:		       19.85
	Successful transactions:       14999
	Failed transactions:	           0
	Longest transaction:	        0.03
	Shortest transaction:	        0.00

### Other usage:
Read the [man page](http://linux.die.net/man/1/siege) or the default --help output, for more usage options.