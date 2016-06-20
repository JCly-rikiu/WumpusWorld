#!/usr/bin/env python

import sys
import telnetlib
import time

HOST = "localhost"

tn = telnetlib.Telnet(HOST,5554)

print "Setting location, press CTRL+C to stop"

while True:
    tn.write("auth QP6FvWSX4G/bd6o8\n")
    tn.write("geo fix 121.541518 25.019428\n")
    time.sleep(5)
    tn.write("geo fix 121.538439 25.019731\n")
    time.sleep(5)
    tn.write("geo fix 121.540133 25.01799\n")
    time.sleep(5)
    tn.write("geo fix 121.53961 25.016862\n")
    time.sleep(5)
    tn.write("geo fix 121.539527 25.016143\n")
    time.sleep(5)

tn.close()
