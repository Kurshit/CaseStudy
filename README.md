# Digital Away Day

This application helps event organizers to organize the given activities among various teams optimally.

# Useful inputs by the organizers and external configuration:
  - By Default, activities start from 9 am and ends at 5 pm with an hour of lunch break around 12 am to 1 pm. Also, the activity start time, end time and lunch time can be configured to the desired values as per user's wish. (Demonstrated in 'DEMO' section.) 
  - Last activity is always an hour long Staff Motivation Presentation.
  - Default input file of activities (Sample Input File - activities.txt) is kept under root location on build path. User can also feed his own input file by providing the path at the command line (Demonstrated  ahead).

### Assumptions
- The number of teams should not be greater than number of activities, as this would lead to making at least one team to remain idle every time.
- An Activity would be part of one team at a time. No two teams would take part in same activity at same time.
- No duplicate activity : An Activity would not be assigned more than once to the same team. There is a data structure that maintains the list of valid and Non-On-Going activities list for each team for allocation.
- Selection of activities (valid activities for that team at that time) is mostly random. However, to utilize the time efficiently  and not letting a team remain idle, for the last one hour before the last activity - 'Staff Motivation Presentation', the assignment of an activity to a team is based on shortest activity time. i.e. A team would be assigned an activity that is shortest of all. Please mind that this scenario has been taken care for the last one hour only. This avoids the starvation and utilizes the total available time. Thus, output could be different every time you run the application and generate the schedule.
For example : If start time is 09:00 am and end time is 05:00 pm, with 'Staff Motivation Presentation' - The last activity- beginning  at 5.00 pm. The teams would be allotted valid activities randomly till 4.00 pm (one hour before any provided end time). After 4.00 pm, each team would be allotted activities with the shortest valid activity available at that time. If the activity would exceed the end time - 5.00 pm, than that activity would not be allotted to the team.


### Design and Framework:

I have used Spring Boot framework and Spring MVC design to develop this application wherein I have let user to configure and feed various inputs as per requirements. The final runnable artifact - production ready artifact- is a jar file with name - "awayday-1.0.0.jar". The application has a REST end point to obtain the solution on browser/postman as well as on console.

The solution has been approached with the intention of making it loosely coupled and abiding to design principles like - Open-Closed and Single Responsibility Principle, keeping the code as flexible as it could.

### Approach:

Following steps shows the brief code flow at higher level:

 - Feed activities.txt input file as input, parse each line using streams and save each activity details into data structure. For each activity, Data structure holds details like - Activity Name, Activity Time, Is Activity Ongoing at given time and List of Teams that have already took part in this particular activity.
 - The REST end point takes 'number of teams' as an input parameter. Take this input and create given no of teams and feed team details into a static data structure. Team details include - Team Name, List of Activities played by each team, On Going Activity of that team at that time, Whether lunch break is done for the team and The end time of currently ongoing activity of each team.
 - Create Schedule - Start with the given start time and for each team, allocate the activity and update the corresponding data structures. At every 5 minutes, check if any team is free and if yes, find any valid activity - that it has not taken part before in - and assign it to the team. The process goes on till the time reaches end time. During the process, if any team gets done with any activity, the team release the activity and marks it free. Also, the checks has been done for given Lunch Time and every team gets an hour of Lunch Break whenever feasible  around Lunch Time.
 - Send output to console and as http response.
 
### Data Structure: 

To maintain the details of each activity and teams, I have chosen Map<String, Map<String,Object>> data structure. Alternate data structure I had in my mind was to store these records as a JSON. Given the performance of Map and java8 streams, I decided to go ahead with Map.

### REST End Point;

Following end point is to be hit to get the schedule.

http://getschedule/{numberOfTeams} 

where "numberOfTeams" is a variable that expects an integer value.

### Tools and Language Used:
    . Java8
    . Gradle

### How to Run The Application : DEMO

 > Prerequisite - Java8 to be installed on the system

#### A. Using default activities.txt input file - that contains 20 sample activities.

This demo takes in default start time = 09:00 am, end time = 5:00 pm,  lunch time = 1:00 pm and default activities.txt file which was provided with case study. However, this could be changed as shown in demo "B" using application.properties file.

 1. Clone the project and get it on your local system.
 2. To get the runnable jar either use the readily created jar present in Root Directory of project - "awayday-1.0.0.jar" or open the command prompt under root directory and run following command -
    ```sh 
    gradlew clean build
    ```
![cleanbuild](https://user-images.githubusercontent.com/30333780/51605616-8f55bf80-1f35-11e9-9e72-f1f6a6f006a4.jpg)
    
3. Above command would create a runnable jar file under "build\libs\awayday-1.0.0.jar".

![artifact](https://user-images.githubusercontent.com/30333780/51605615-8f55bf80-1f35-11e9-814e-c0107306182f.jpg)

4. Run this jar using following command 
   ```sh
   java -jar awayday-1.0.0.jar
   ```
![run app](https://user-images.githubusercontent.com/30333780/51605620-8fee5600-1f35-11e9-8ede-c9f27b314672.jpg)
   
5. This would start the application on 8080 port by default. This would consider default activities.txt file as input which is already on build path under "resource/activities.txt" location.
6. Open any browser, preferably  Google Chrome, and hit the below rest end point. 
    
    http://localhost:8080/getschedule/2 

    Note: "2" is the no of teams taken here as sample input. You can choose to provide any number of teams, where number of teams should be less than number of activities defined.   
  
7. The output schedule of activities shall be visible on the browser itself as well as on command prompt console.


![output on browser](https://user-images.githubusercontent.com/30333780/51605617-8fee5600-1f35-11e9-9d1d-7213d75a01bb.jpg)

![output on console](https://user-images.githubusercontent.com/30333780/51605618-8fee5600-1f35-11e9-8039-add3b5ba4e24.jpg)


 #### B. Using user provided input file "activities.txt" with any number of activities
 
 This demo requires user to provide file path of "activities.txt" present on user's system. This input is fed to application on command prompt which typically injects the application.properties value in the application.
 
 1. 1. Clone the project and get it on your local system
 2. To get the runnable jar wither use the readily created jar present in Root Directory of project - "awayday-1.0.0.jar" or run command prompt under root directory and run following command -
    ```sh 
    gradlew clean build
    ```
3. Above command would create a runnable jar file under "build\libs\awayday-1.0.0.jar".
4. Run this jar and provide file path as per below command. Let's assume that user has kept "activities.txt" input file at "D:\input\activities.txt". Similarly, we can provide schedule start time and end time. This is discussed in "Externalizing configuration" section below.
   ```sh
   java -jar awayday-1.0.0.jar --fileName="D:\input\activities.txt"
   ```
   
 ![run app with file](https://user-images.githubusercontent.com/30333780/51605619-8fee5600-1f35-11e9-9fd8-2887668eb180.jpg)
   
5. This would start the app on 8080 port by default. This would consider activities.txt file as input which is present at user's own local system. 
6. Open any browser , preferably  Google Chrome, and hit the below rest end point. 

    http://localhost:8080/getschedule/2 

    Note: "2" is the no of teams taken here as sample. You can choose to provide any number of teams, where number of teams should be less than number of activities defined.
7. The output schedule of activities shall be visible on the browser itself as well as on command prompt console.

#### Externalizing configuration :

Just like the acitivites.txt input file, default startTime, endTime and lunch time can be overridden while running the application through command prompt. Run the following command to run ass with user values.

```sh
java -jar awayday-1.0.0.jar --fileName="D:\input\activities.txt" --startTime=11:00 --endTime=19:00 --lunchTimeStartsAt=14:00 lunchTimeEndsAt=15:00
```

Where start time and end time can be anything but in 24 hour format.

Also, We can provide these values in application.properties file itself instead of passing them over command prompt, as shown below.
