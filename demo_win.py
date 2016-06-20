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
    tn.write("geo fix 121.540572 25.020488\n")
    time.sleep(5)
    tn.write("geo fix 121.539871 25.020888\n")
    time.sleep(5)
    tn.write("geo fix 121.535189 25.021651\n")
    time.sleep(5)
    tn.write("geo fix 121.537628 25.020344\n")
    time.sleep(5)
    tn.write("geo fix 121.538439 25.019731\n")
    time.sleep(5)
    tn.write("geo fix 121.540133 25.01799\n")
    time.sleep(5)
    tn.write("geo fix 121.542338 25.019855\n")
    time.sleep(5)
    tn.write("geo fix 121.541965 25.017755\n")
    time.sleep(5)
    tn.write("geo fix 121.540573 25.017286\n")
    time.sleep(10)
    tn.write("geo fix 121.539527 25.016143\n")
    time.sleep(5)
    tn.write("geo fix 121.537634 25.015173\n")
    time.sleep(10)

tn.close()
