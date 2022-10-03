Commands for mass import:

(cat names_counting.txt; sleep 10) | nc localhost 6379
**However this is not a very reliable way to perform mass import because netcat does not really know when all the data was transferred and can't check for errors.

[OR]

cat names_counting.txt | redis-cli