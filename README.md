[![Build Status](https://travis-ci.com/ingomohr/docwriter.svg?branch=master)](https://travis-ci.com/ingomohr/trac)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
### Motivation
Trac is a small tool to read working time protocols and turn them into analyzable models.

#### Protocols Example
The following protocols block can be imported as model of two protocols.

```
Tue (May 18 2021)
---
08:30 Entry A
08:45-10:00 Entry B


Mo (May 17 2021)
---
08:30 Entry A
08:45-10:00 Entry B
```

##### Format Specs
- Each line is one entry
- Each line that starts with a time info (simple - as for "Entry A" - or full - as for "Entry B") is a worklog entry
- All other lines are meta-entries
- For each entry, the full text is imported (including any text you might consider comments)
- If a protocol starts with a meta-entry, that first meta-entry is the protocol title
- A protocol ends at an empty line or when the input document ends

