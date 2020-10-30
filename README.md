[![Build Status](https://travis-ci.com/ingomohr/docwriter.svg?branch=master)](https://travis-ci.com/ingomohr/trac)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
### Motivation
Trac is a small tool to read working time protocols and turn them into analyzable models.

#### Example-Format of the Protocol
```
Mo (Mar 9 2020)
---
08:27 Topic A                  // item from 08:27 to 08:44, worked on "Topic A"
08:44-08:57 Topic B            // item from 08:44 to 08:57, worked on "Topic B"
09:25-09:45 Topic C: Topic C1  // item from 09:25 to 09:45, worked on "Topic C1" as sub-topic of "Topic C"
```

#### Example
Input protocol:
```
Fr (Oct 30 2020)
---
08:30 Meeting: Daily Standup
08:45 Dev: ProjectX: BugfixY
09:24 Orga: Review GTD items
09:35 Dev: ProjectX: BugfixZ
10:24 Break
10:45-11:50 Doc: ProjectX: Complete Manual
```

Analysis via ``TracProtocolWriterByTopic``:

```
Protocol by Topics
-----------------------
Start : 08:30 : Fr (Oct 30 2020)
End   : 11:50 : Fr (Oct 30 2020)
-----------------------
Total time      :  3:20
Breaks          :  0:21
Time w/o breaks :  2:59
-----------------------
1:28 ########............  44% Dev
1:05 ######..............  33% Doc
0:21 ##..................  11% Break
0:15 #...................   8% Meeting
0:11 #...................   6% Orga
```


