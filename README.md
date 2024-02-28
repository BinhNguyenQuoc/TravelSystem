# HypotheticalTravelSystem

Key Concepts:
-	Touch On: On boarding a bus, passengers taps their credit card (identified by a Hashed Number called as Primary Account Number) which is called as Touch On.
-	Touch Off: When passenger gets off the bus, they tap their card again which is called a Touch Off.
-	Amount to Charge: The amount to charge the passenger will be determined where they Touch On and where the Touch Off. The amount is determined as follows:
     o	Trip Between Stop A and Stop B costs $4.50
     o	Trip Between Stop B and Stop C costs $6.25
     o	Trip Between Stop A and Stop C costs $8.45
-	Travel Direction:  The above Amount to Charge applies to travel in either direction. This means that the same amount is charged if a passenger Touch On at Stop A and Touch Off at Stop B OR they can Touch On at Stop B and Touch Off at Stop A.
     Types of Trips
-	Completed Trips: If the passenger Touch On at one stop and Touch Off at another stop, this is treated as a completed trip. The amount to charge the passenger is determined by the above Amount to Charge section. E.g: Touch On at Stop A and Touch Off at Stop C is a completed trip and passenger is charged $8.45.
-	Incomplete Trips: If the passenger Touch On at one stop and forget to Touch Off at another stop, this is treated as an incomplete trip. The passenger in this case is charged the maximum possible fare, where they could have travelled to. Eg: A passenger Touch On at Stop B and does not Touch Off, they could travelled to either Stop A ($4.5) or Stop C ($6.25). In this case, they will be charged the higher value ($6.25).
-	Cancelled Trip: If the passenger Touch On and Touch Off at the same stop, this is called a cancelled trip and the passenger would not be charged.

## Additional Data Assumption:
- 1: File touchData.csv is already sorted by datetime, companyId, Touch On then Touch Off
- 2: One companyID can go back and forth many times in 1 date - see Company1. But the report should exclude duplicate record.
- 3: The complete trip is considered by the data of the OFF touch, it means if the PAN is wrong in the ON trip, it is successful in the OFF trip.
- 4: To export to file with a good data view, data is sorted by date and company and bus in advance


## Implementation:
- To have less code to read data from file to java object, we are using Opencsv to mapping the data with customized fields.
- We assume data is less than 50MB to easy handle it in memory than using more streaming approach like kafka, database to store it
- Base on Java feature, we use the Java stream to group data by date, company, bus and the total chargeAmount for 1 bus
- If we need to process multiple files and big files, we have to change to use kafka or store database then get data from database, or we can use python to process file better the send to kafka or call RESTFul API to Java process each record.

## Building

To build the entire Gradle project, you should run the following in the root of the checkout, then run:

     ./gradlew build

## Run the application:
Started a Spring Boot application via:

     ./gradlew bootrun

## Output:
The files exported will be placed in the output folder under the project.