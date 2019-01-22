package com.delloite.awayday.service;

import static com.delloite.awayday.data.Constants.ACTIVITIES_PLAYED_LIST;
import static com.delloite.awayday.data.Constants.ACTIVITY_TIME;
import static com.delloite.awayday.data.Constants.COLUN;
import static com.delloite.awayday.data.Constants.CURRENT_ACTIVITY_ENDS_AT;
import static com.delloite.awayday.data.Constants.EMPTY_STRING;
import static com.delloite.awayday.data.Constants.IS_LUNCH_BREAK_DONE;
import static com.delloite.awayday.data.Constants.IS_ONGOING;
import static com.delloite.awayday.data.Constants.LUNCH_BREAK;
import static com.delloite.awayday.data.Constants.LUNCH_BREAK_TIME;
import static com.delloite.awayday.data.Constants.NEW_LINE;
import static com.delloite.awayday.data.Constants.ON_GOING_ACTIVITY;
import static com.delloite.awayday.data.Constants.PLAYED_BY_TEAMS_LIST;
import static com.delloite.awayday.data.Constants.SPACE;
import static com.delloite.awayday.data.Constants.SPACE_COLUN_SPACE;
import static com.delloite.awayday.data.Constants.STAFF_MOTIVATION_PRESENTATION;
import static com.delloite.awayday.data.Constants.TEAM;
import static com.delloite.awayday.data.Constants.TWELVE_HR_TIME_FORMATE;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class ScheduleService {

	private static Map<String, Map<String, Object>> activities = new HashMap<>();

	private static Map<String, Map<String,Object>> teams = new HashMap<>();

	private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

	@Value("${startTime:09:00}")
	private String START_TIME;

	@Value("${endTime:17:00}")
	private String END_TIME;

	@Value("${lunchTimeStartsAt:12:00}")
	private String LUNCH_TIME_STARTS_AT;

	@Value("${lunchTimeEndsAt:13:00}")
	private String LUNCH_TIME_ENDS_AT;

	public void parseActivities(String fileNameAndPath) {
		try {
			if(fileNameAndPath.isEmpty()) {
				File file = ResourceUtils.getFile("classpath:inputfile/activities.txt");

				if(file == null) {
					logger.error("Input File is not present on classpath");
					throw new FileNotFoundException("Input File is not present on classpath");
				}

				fileNameAndPath = file.getPath();
			}
			logger.info("File path is " + fileNameAndPath);
			Stream<String> stream = Files.lines(Paths.get(fileNameAndPath));
			stream.forEach(line -> {
				Map<String, Object> activityDetails = new HashMap<>();
				int minIndex = line.indexOf("min");
				String activityName = minIndex !=-1 ? line.substring(0,minIndex - 2).trim() : line.substring(0,line.indexOf("sprint") - 2).trim();
				int minutes = minIndex !=-1 ? Integer.parseInt(line.substring(minIndex - 2, minIndex).trim()) : 15;
				activityDetails.put(ACTIVITY_TIME, minutes);
				activityDetails.put(IS_ONGOING, false);
				activityDetails.put(PLAYED_BY_TEAMS_LIST, new ArrayList<String>());
				activities.put(activityName, activityDetails );
			});

		} catch (FileNotFoundException e) {
			logger.error("Input File does not exist at provided path");
		} catch (IOException e) {
			logger.error("Unexpected error occured.");
		}		

	}

	public void createTeams(final int totalTeams) {
		for(int i = 1; i<= totalTeams; i++) {
			Map<String, Object> teamDetails = new HashMap<>();
			teamDetails.put(CURRENT_ACTIVITY_ENDS_AT, LocalTime.parse(START_TIME));
			teamDetails.put(ON_GOING_ACTIVITY, "");
			teamDetails.put(ACTIVITIES_PLAYED_LIST, new ArrayList<String>());
			teamDetails.put(IS_LUNCH_BREAK_DONE, Boolean.FALSE);
			teams.put(TEAM+i, teamDetails);			
		}
	}

	public String createSchedule(int totalTeams) {
		List<StringBuilder> scheduleStringBuilderList = new ArrayList<>();
		for(int i=0; i< totalTeams; i++) {
			scheduleStringBuilderList.add(new StringBuilder().append(TEAM).append(i+1).append(COLUN));
		}
		try {
			LocalTime startTime = LocalTime.parse(START_TIME);
			LocalTime endTime = LocalTime.parse(END_TIME); 
			while(startTime.compareTo(endTime) !=0) {
				for(int i=0; i< totalTeams; i++) {
					HashMap<String,Object> teamsDetailsMap = (HashMap<String, Object>) teams.get(TEAM+(i+1));
					LocalTime currentActivityEndsAt = (LocalTime) teamsDetailsMap.get(CURRENT_ACTIVITY_ENDS_AT);
					releaseActivitiesDoneAtThisHour(startTime);
					boolean isLunchTime = false;
					if(isThisLunchTime(startTime)) {
						isLunchTime = true;
					}
					if(startTime.compareTo(currentActivityEndsAt) >= 0) {
						String validActivity = getValidActivityNameForTeam(TEAM+(i+1), startTime, endTime);
						StringBuilder scheduleStringBuilderForTeamI = scheduleStringBuilderList.get(i);
						String startTimeString = startTime.format(DateTimeFormatter.ofPattern(TWELVE_HR_TIME_FORMATE));
						if(!validActivity.isEmpty()) {						
							if(isLunchTime && !(Boolean) teamsDetailsMap.get(IS_LUNCH_BREAK_DONE)) {
								scheduleStringBuilderForTeamI.append(NEW_LINE).append(startTimeString).append(SPACE_COLUN_SPACE).append(LUNCH_BREAK).append(SPACE).append(LUNCH_BREAK_TIME);
								teamsDetailsMap.put(CURRENT_ACTIVITY_ENDS_AT, startTime.plus(60, ChronoUnit.MINUTES));
								teamsDetailsMap.put(IS_LUNCH_BREAK_DONE, true);
							}
							else {
								Map<String, Object> activitiesMap = activities.get(validActivity);
								LocalTime newActivityEndsAt = startTime.plus((Integer)activitiesMap.get("time"), ChronoUnit.MINUTES);
								if(newActivityEndsAt.compareTo(endTime)>0)
									continue;

								int activityTimeInMin = (int)activitiesMap.get("time");
								String minOrSprint = activityTimeInMin == 15 ? "sprint" : activityTimeInMin + "min";
								scheduleStringBuilderForTeamI.append(NEW_LINE).append(startTimeString).append(SPACE_COLUN_SPACE).append(validActivity).append(SPACE).append(minOrSprint);
								activitiesMap.put(IS_ONGOING, true);
								List<String> list = (List<String>) activitiesMap.get(PLAYED_BY_TEAMS_LIST);
								list.add(TEAM+(i+1));
								activitiesMap.put(PLAYED_BY_TEAMS_LIST, list);
								activities.put(validActivity, activitiesMap);
								teamsDetailsMap.put(CURRENT_ACTIVITY_ENDS_AT, startTime.plus((Integer)activitiesMap.get("time"), ChronoUnit.MINUTES));
								List<String> playedActivitiesList = (List<String>) teamsDetailsMap.get(ACTIVITIES_PLAYED_LIST);
								playedActivitiesList.add(validActivity);
								teamsDetailsMap.put(ACTIVITIES_PLAYED_LIST,playedActivitiesList);
								teamsDetailsMap.put(ON_GOING_ACTIVITY, validActivity);
								teams.put(TEAM+(i+1), teamsDetailsMap);
							}
						} else {
							LocalTime flexiblePreLunchTime = LocalTime.parse(LUNCH_TIME_STARTS_AT).minus(30,ChronoUnit.MINUTES);
							LocalTime flexiblePostLunchTime = LocalTime.parse(LUNCH_TIME_ENDS_AT).plus(30,ChronoUnit.MINUTES);
							if(isLunchTime || (startTime.compareTo(flexiblePreLunchTime)>=0 && startTime.compareTo(flexiblePostLunchTime)<=0 && 
									!(Boolean) teamsDetailsMap.get(IS_LUNCH_BREAK_DONE))) {
								scheduleStringBuilderForTeamI.append(NEW_LINE).append(startTimeString).append(SPACE_COLUN_SPACE).append(LUNCH_BREAK).append(SPACE).append(LUNCH_BREAK_TIME);
								teamsDetailsMap.put(CURRENT_ACTIVITY_ENDS_AT, startTime.plus(60, ChronoUnit.MINUTES));
								teamsDetailsMap.put(IS_LUNCH_BREAK_DONE, true);
							}
							else 
								logger.info("No valid free activity at this " + startTime + " hour. Team would remain idle till one of the activity gets free");
						}
					}
				}
				startTime = startTime.plus(5,ChronoUnit.MINUTES);
			}
			
			StringBuilder finalScheduleString = new StringBuilder();
			
			for(StringBuilder s : scheduleStringBuilderList) {
				s.append(NEW_LINE).append(endTime.format(DateTimeFormatter.ofPattern(TWELVE_HR_TIME_FORMATE))).append(SPACE_COLUN_SPACE).append(STAFF_MOTIVATION_PRESENTATION);
				System.out.println(s);
				System.out.println();
				finalScheduleString.append("\n").append(s).append("\n");
			}
			
			return finalScheduleString.toString();

		} catch(Exception e) {
			logger.error("Unexpected error occured");
			return "";
		}		

	}

	public void releaseActivitiesDoneAtThisHour(LocalTime startTime) {
		Collection<Map<String, Object>> mapCollection = teams.values();
		Iterator<Map<String,Object>> itr = mapCollection.iterator();
		while(itr.hasNext()) {
			Map<String,Object> teamsDetailsMap = itr.next();
			LocalTime currentActivityEndsAt = (LocalTime) teamsDetailsMap.get(CURRENT_ACTIVITY_ENDS_AT);
			if(startTime.compareTo(currentActivityEndsAt) >= 0) {
				String onGoingActivity = (String) teamsDetailsMap.get(ON_GOING_ACTIVITY);
				if(!onGoingActivity.isEmpty()) {
					Map<String, Object> onGoingActivityMap = activities.get(onGoingActivity);
					if((Boolean) onGoingActivityMap.get(IS_ONGOING)) {
						onGoingActivityMap.put(IS_ONGOING, false);
						teamsDetailsMap.put(ON_GOING_ACTIVITY, EMPTY_STRING);
					}

				}
			}		

		}		
	}


	public String getValidActivityNameForTeam(final String teamName, LocalTime startTime, LocalTime endTime) {
		List<String> validActivityList = new ArrayList<>();
		String shortestActivity = "";
		int shortestActivityTime = 3600;
		for(Map.Entry<String, Map<String, Object>> entry : activities.entrySet()) {
			Map<String, Object> activityDetailsMap = entry.getValue();
			List<String> list = (List<String>)activityDetailsMap.get(PLAYED_BY_TEAMS_LIST);
			if(! (Boolean)activityDetailsMap.get(IS_ONGOING) && !(Boolean) list.contains(teamName)){
				int activityTime = (int)activityDetailsMap.get(ACTIVITY_TIME);
				if(activityTime < shortestActivityTime) {
					shortestActivityTime = activityTime;
					shortestActivity = entry.getKey();
				}
				validActivityList.add(entry.getKey());
			}
		}

		if(startTime.compareTo(endTime.minus(60, ChronoUnit.MINUTES)) >=0) {
			if(!validActivityList.isEmpty() && validActivityList.contains(shortestActivity)) {
				return shortestActivity;			
			} else {
				return "";
			}
		}

		Random random = new Random();
		if(!validActivityList.isEmpty()) {
			int randomIndex = random.nextInt(validActivityList.size());
			return validActivityList.get(randomIndex);			
		} else {
			return "";
		}

	}

	public boolean isThisLunchTime(LocalTime startTime) {
		return startTime.compareTo(LocalTime.parse(LUNCH_TIME_STARTS_AT)) >=0 && startTime.compareTo(LocalTime.parse(LUNCH_TIME_ENDS_AT)) <=0; 
	}



}
