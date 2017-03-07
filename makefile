JFLAGS = -d
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) . $*.java

CLASSES = \
	TokenKind.java \
        Tokenizer.java \
	LispTokenizer.java \
        TokenizerTest.java \
        sexpression.java \
        Parser.java \
        Parsertest.java
        
       	 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class