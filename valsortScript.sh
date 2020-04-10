#!/bin/bash

sort 1GB -S 8G > sorted1G; ./valsort sorted1G &>> ValsortLinux.log
sort 4GB -S 8G > sorted4G; ./valsort sorted4G &>> ValsortLinux.log
sort 16GB -S 8G > sorted16G; ./valsort sorted16G &>> ValsortLinux.log
sort 64GB -S 8G > sorted64G; ./valsort sorted64G &> ValsortLinux.log

