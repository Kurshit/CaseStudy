# Delloite Digital Away Day

This application helps event organizers to organize the given activities amongts various teams optimally.

# Useful inputs by the organizers and external configuration:
  - By Default, activities start from 9 am and ends at 5 pm with an hour of lunch break around 12 am to 1 pm. Also, the activity start time, end time and lunch time can be configured to the desired values as per user's wish. (Demonstrated in 'DEMO' section.) 
  - Last activity is always an hour long Staff Presentation.
  - Default input file of activities (Sample Input File) is kept under 'resource/inputfile/activities.txt' location on build path. User can also feed his own input file by providing the path at the command line (Denomnstrated ahead).

### Assumptions
-- The number of teams should not be greater than no of activities, as this would lead at least one team remain idle everytime.

### Design, Framework and Approach:

I have used Spring Boot framework and Spring MVC design to develop this application wherein I have let user to configure and feed various inputs as per requirements. The final runnable artifect - production ready- is a jar file with name - "awayday-1.0.0.jar".
The solution has been approached with the intention of making loosely coupled code and abiding to design principles like - Open-Closed and Single Responsibility Principle, keeping the code as flexible as it could.  

