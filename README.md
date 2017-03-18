# LaTeXbuilder

LaTeXbuilder facilitates the compilation of latex source code into PDF or PNG file format e.g. for the creation of a PNG image file of a math formula.

**Features (to be extended):**

- Build a PDF or PNG from given LaTeX source code, e.g. a simple formula such as `$e^{i\pi} + 1 = 0$`
- Embed the source code in the PNG file for future modifying of the contents
- Build figures created with `tikzpicture` and/or `pgfplots`
- (*Experimental*) Parse latex code from within ASCII encoded files that contain other arbitrary characters besides LaTeX source code (e.g., source file written in some other programming language)

A command line interface and rudimentary GUI are available.

The program has been tested with *Ubuntu 14.04* and *TeX Live*, and *Windows 7* and *MiKTeX*.

**Libraries used:**

- [icafe](https://github.com/dragon66/icafe), a Java library for reading, writing, converting, and manipulating images and meta data. The library is used to embed the LaTeX source code within the built PNG file and to extract the embedded code in PNGs
- [JOpt Simple](https://pholser.github.io/jopt-simple/), a command line option parsing library
- [ini4j](http://ini4j.sourceforge.net/), a Java API for parsing configuration files written in the .ini file format

## Requirements

For the program to work, the following is necessary:
- A latex distribution that contains a *pdflatex* implementation (e.g. MiKTeX or TeX Live)
- ImageMagick for the conversion from PDF to PNG
- *Win 7 users*: The path to `convert.exe` provided by ImageMagick must be given in `config.ini` (e.g. `
path = C:\Program Files (x86)\ImageMagick-6.9.0-Q16\`) under section `imagemagick`

## (*Experimental*) Parsing ASCII file & building LaTeX content
The LaTeX code to be read from within an arbitraty ASCII file must be embedded within the following XML tags:
```xml
<latex>
	<file> code.png </file>
	<code>
		$e^{i\pi} + 1 = 0$
	</code>
</latex>
```
At the current stage, the parser only scans for the above tags.

## Examples

### Building a simple plot created with *tikzpicture*
The command line interface is used to compile sample code consisting of a plot (http://pgfplots.sourceforge.net/gallery.html).
Source file (pgfplots_example.tex):
```latex
\begin{tikzpicture}
	\begin{axis}[
		xlabel=$x$, ylabel={$f(x) = x^2 - x +4$} ]
		% use TeX as calculator:
		\addplot {x^2 - x +4};
	\end{axis}
\end{tikzpicture}
```

Command to build *pgfplots_example.tex*:

`LaTeXbuilder.jar -b pgfplots_example.tex -o pgfplots_example.png`

Result:

![Result](pgfplots_example.png)
