CC = g++
CFLAGS = -I/usr/include/cairomm-1.0 -I/usr/include/sigc++-2.0 -I/usr/lib/sigc++-2.0/include -I/usr/include/cairo -I/usr/include/freetype2 -I. -g
LIBS = -lQtCore -lQtXml -lQtXmlPatterns -lcola -lvpsc -ltopology -lcairomm-1.0 -lxerces-c -lsbgn -lboost_graph

ALL_SOURCE = $(wildcard *.cpp)
ALL_OBJ = $(ALL_SOURCE:%.cpp=%.o)
ALL_SBML = $(wildcard models/*.sbml.xml)
ALL_SBGN = $(ALL_SBML:%.sbml.xml=%.sbgn.xml)
ALL_PNG = $(ALL_SBGN:%.sbgn.xml=%.png)

all: $(ALL_OBJ)
	$(CC) -o SBMLQual2SBGN $(ALL_OBJ) $(LIBS)

convert: $(ALL_SBGN) $(ALL_PNG)

clean:
	rm -f $(ALL_OBJ) $(ALL_SBGN) $(ALL_PNG) SBMLQual2SBGN

%.o: %.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

%.sbgn.xml: %.sbml.xml
	./SBMLQual2SBGN $^ $@

%.png: %.sbgn.xml
	java -jar sbgn2png/SBGN.jar $^ $@ 
