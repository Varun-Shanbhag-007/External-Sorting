JC = javac

MySort: MySort.class

MySort.class: MySort.java
	$(JC) MySort.java SortHelper.java InMemorySortHelper.java

clean:
	rm *.class
