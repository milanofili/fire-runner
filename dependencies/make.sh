#!/bin/sh

# make protobuf
cd protobuf
./configure
make
#sudo make install
cd python
sudo python setup.py install
cd ..


