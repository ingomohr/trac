![Build](https://github.com/ingomohr/trac/actions/workflows/maven.yml/badge.svg?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
## Motivation
Trac is a small tool to read working time protocols and turn them into analyzable models.

## Trac File Format
A trac file is any text file that follows these rules:

* Each line is one entry
* Empty lines separate protocols
* First line in a protocol is the protocol title
* "#" starts a comment
* Lines of only "-" chars can be used so separate data
* Work log entries have a start time and optionally an end time
    * If only start time is specified the end time will be the start time of the successor work log entry
    * Start times and End time is given as HH:mm
        * e.g. 23:15, 00:20
    * Work log entries with text "break" (case ignore) are considered as breaks
### Work Log Entry Format:

``<HH:mm>-<HH:mm> <Entry text>``

or (if there is a successor work log entry):

``<HH:mm> <Entry text>``

## How to Run
_For now, there is no release of Trac. You can, however, create a build and execute it._

* Have Java 17 installed on your machine
* Open your terminal and enter command:
```
java -jar trac-1.0-SNAPSHOT.jar -path /path/to/trac.txt -p timeSpent
```
* **path**: specifies the path of the protocols file to read
* **p**: specifies the profiles to be called to analyze the data
    * Available profiles
        * **timeSpent**: for each protocol, this shows
            * start time
            * end time
            * effective working time

### Alternative: Use the trac.sh
The repo also contains a ``trac.sh`` which works on macOS (and should work on Linux, too).

It accesses the build in the ``/target`` folder that is created when building the repo.

```
./trac.sh /path/to/trac.txt -p timeSpent
```

### Example

Protocols file:
```
Tue (May 18 2021)
# sunny day
---
08:30 Did something
09:00 Break
09:30-11:30 Did something else


Mo (May 17 2021)
---
08:30 Did something
08:45-10:00 Did something else
```

Call:

```
java -jar trac-1.0-SNAPSHOT.jar -path /path/to/trac.txt -p timeSpent
```

Result:
```
Trac 1.0.0
# Time spent

## Protocol 1: Tue (May 18 2021)

    - Start               : 08:30
    - End                 : 11:30
    - Effective time spent: 2h 30m
        - i.e. w/o breaks

## Protocol 2: Mo (May 17 2021)

    - Start               : 08:30
    - End                 : 10:00
    - Effective time spent: 1h 30m
        - i.e. w/o breaks

```

## How to Build
* Have Java 17 SDK installed
* Have Maven installed
* Open your terminal and call ``mvn package``
    * This will build and test the code
    * In the ``/target`` folder you'll find the JAR











