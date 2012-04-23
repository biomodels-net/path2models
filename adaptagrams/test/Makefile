CC = g++
CFLAGS = -I/usr/include/cairomm-1.0 -I/usr/include/sigc++-2.0 -I/usr/lib/sigc++-2.0/include -I/usr/include/cairo -I/usr/include/freetype2 -I.
LIBS = -lcola -lvpsc -lavoid -lproject -ltopology -lcairomm-1.0 -lboost_regex

ALL_SOURCE = $(wildcard *.cpp)
ALL_OBJ = $(ALL_SOURCE:%.cpp=%.o)
ALL_EXEC = $(ALL_SOURCE:%.cpp=%.app)

all: $(ALL_EXEC)

%.app: %.o
	$(CC) -o $@ $^ $(LIBS)

%.o: %.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

clean:
	rm -f $(ALL_OBJ) $(ALL_EXEC)

