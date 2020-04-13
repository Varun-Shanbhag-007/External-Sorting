JC = javac

Driver: Driver.class 

Driver.class: Driver.java 
	$(JC) Driver.java SortHelper.java InMemorySortHelper.java

clean:
	rm *.class
