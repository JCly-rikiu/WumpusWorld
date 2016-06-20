#!/usr/bin/env python

import sys
import telnetlib
import time

HOST = "localhost"

tn = telnetlib.Telnet(HOST,5554)

print "Setting location, press CTRL+C to stop"

tn.write("auth QP6FvWSX4G/bd6o8\n")
tn.write("geo fix 121.541518 25.019428\n")
time.sleep(5)
tn.write("geo fix 121.538439 25.019731\n")
time.sleep(5)
tn.write("geo fix 121.540133 25.01799\n")
time.sleep(10)
tn.write("geo fix 121.534694 25.0142002\n")
time.sleep(5)

tn.close()
