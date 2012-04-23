CC = g++
CFLAGS = -I.
LIBS = -lQtCore -lQtXml -lQtXmlPatterns

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

