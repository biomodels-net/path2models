CC = g++
CFLAGS = -I/usr/include/cairomm-1.0 -I/usr/include/sigc++-2.0 -I/usr/lib/sigc++-2.0/include -I/usr/include/cairo -I/usr/include/freetype2 -I.
LIBS = -lQtCore -lQtXml -lQtXmlPatterns -lcola -lvpsc -ltopology -lcairomm-1.0 -lxerces-c -lsbgn

ALL_SOURCE = $(wildcard *.cpp)
ALL_OBJ = $(ALL_SOURCE:%.cpp=%.o)
ALL_EXEC = $(ALL_SOURCE:%.cpp=%.app)
ALL_SBML = $(wildcard *.sbml.xml)
ALL_SBGN = $(ALL_SBML:%.sbml.xml=%.sbgn.xml)

all: $(ALL_EXEC) $(ALL_SBGN)

%.app: %.o
	$(CC) -o $@ $^ $(LIBS)

%.o: %.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

clean:
	rm -f $(ALL_OBJ) $(ALL_EXEC)

%.sbgn.xml: %.sbml.xml
	./main.app $^ $@
