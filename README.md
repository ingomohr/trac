[![Build Status](https://travis-ci.com/ingomohr/docwriter.svg?branch=master)](https://travis-ci.com/ingomohr/trac)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)
### Motivation
Trac is a small tool to read working time protocols and turn them into analyzable models.

#### Example-Format of the Protocol
```
Mo (Mar 9 2020)
---
08:27 Topic A               // item from 08:27 to 08:44, worked on "Topic A"
08:44-08:57 Topic B         // item from 08:44 to 08:57, worked on "Topic B"
09:25-45 Topic C: Topic C1  // item from 09:25 to 09:45, worked on "Topic C1" as sub-topic of "Topic C"
```