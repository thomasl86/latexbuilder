# LaTeXbuilder

LaTeXbuilder facilitates the compilation of latex source code into PDF or PNG file format e.g. for the creation of a PNG image file of a math formula.
It can
- build a PDF or PNG from given LaTeX source code, e.g. a simple formua: `$\sum P = 0$`
- embed the source code in the PNG file for future modifying of the PNG contents
- build figures created with tikzpicture and/or pgfplots

A commandline interface and rudimentary GUI are available (so far).

The program has so far only been tested with Ubuntu 14.04.

# Requirements

For the program to work, the following needs to be pre-installed:
- A latex distribution that contains a pdflatex implementation (e.g. MiKTeX or TeX Live)
- ImageMagick for the conversion from PDF to PNG

The program uses the following libraries:
- The command line parsing library JOpt Simple
- icafe (https://github.com/dragon66/icafe), a Java library for reading, writing, converting and manipulating images and metadata. The library is used to embed the LaTeX source code within the built PNG file
-ini4j, a Java AIP for parsing configuration files written in the .ini file format
