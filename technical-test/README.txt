--------------------CONCERNS-------------------------
1. Consistency between example 4,5 and 6: In example 4 we have have the below;

1.1	Update: friend
1.1.1	Create: Person
1.1.1.1	       Create: firstName as “Tom”

Example 5 and 6 should also be as below;

1.2	Update: friend
1.2.1	Update: Person
1.2.1.1	 	Update: firstName from “Tom” to “Jim

--------------------ASSUMPTIONS-------------------------

1.	Properties of original or modified that are collections, arrays and maps will only contain primitive types