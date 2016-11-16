# DeDeSim
Digital Electronics Discrete Event Simulation

Similar to SystemC, DeDeSim is a tool to build architectural models.
At this point it is nothing more than an example of circuit simulation
from book "Programming in Scala".

## Setup GIT

```
> mkdir -p path/dedesim/dedesim
> cd path/dedesim/dedesim
> git init
> git remote add origin https://github.com/curoles/dedesim.git
> git pull origin master
```

## Prepare to build

Create a build directory somewhere and cd into it:
```
> mkdir build
> cd build
```

Run configuration script:
```
> bash <path tp dedesim>/configure.bash 
Build environment configuration BASH script.
Current working directory=/home/igor/prj/github/dedesim/build
Source path=../dedesim
Absolute Source path=/home/igor/prj/github/dedesim/dedesim
JAVAC=/usr/bin/javac
SCALAC=/usr/bin/scalac
FASTSCALAC=/usr/bin/fsc
```

Check that file called 'Makefile' was generated:
```
> ls
config.makefile  Makefile
```

## Build

Issue following command inside the build directory:
```
> make
```

To run tests:
```
> make test
```

To generate documentation:
```
> make doc
```
