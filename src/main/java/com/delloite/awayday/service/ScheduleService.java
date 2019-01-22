package com.delloite.awayday.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

	static Map<String, Map<String, Object>> activities = new HashMap<>();

	static Map<String, Map<String,Object>> teams = new HashMap<>();

	public void parseActivities(final String fileName) {

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			stream.forEach(line -> {
				Map<String, Object> activityDetails = new HashMap<>();
				int minIndex = line.indexOf("min");
				String activityName = minIndex !=-1 ? line.substring(0,minIndex - 2).trim() : line.substring(0,line.indexOf("sprint") - 2).trim();
				int minutes = minIndex !=-1 ? Integer.parseInt(line.substring(minIndex - 2, minIndex).trim()) : 15;
				activityDetails.put("time", minutes);
				activityDetails.put("isOngoing", false);
				activityDetails.put("playedByTeamsList", new ArrayList<String>());
				activities.put(activityName, activityDetails );
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}		

	}

	public void createTeams(final int totalTeams) {

		for(int i = 1; i<= totalTeams; i++) {

			Map<String, Object> teamDetails = new HashMap<>();
			teamDetails.put("currentActivityEndsAt", LocalTime.parse("09:00"));
			teamDetails.put("onGoingActivity", "");
			teamDetails.put("activitiesPlayedList", new ArrayList<String>());
			teams.put("Team"+i, teamDetails);			
		}	

	}

	public void createSchedule(int totalTeams) {
			
		
		List<StringBuilder> scheduleStringBuilderList = new ArrayList<>();
		
		for(int i=0; i< totalTeams; i++) {
			scheduleStringBuilderList.add(new StringBuilder().append("Team").append(i+1).append(":"));
		}
		
		LocalTime startTime = LocalTime.parse("09:00");
		LocalTime endTime = LocalTime.parse("12:00"); 
		int result = 0;
		while((result = startTime.compareTo(endTime)) !=0) {
			
			for(int i=0; i< totalTeams; i++) {
				
				HashMap<String,Object> teamsDetailsMap = (HashMap<String, Object>) teams.get("Team"+(i+1));
				
				LocalTime currentActivityEndsAt = (LocalTime) teamsDetailsMap.get("currentActivityEndsAt");
				
				releaseActivitiesDoneAtThisHour(startTime);
								
				if(startTime.compareTo(currentActivityEndsAt) >= 0) {
					
					
					/*
					String onGoingActivity = (String) teamsDetailsMap.get("onGoingActivity");
					if(!onGoingActivity.isEmpty()) {
						Map<String, Object> onGoingActivityMap = activities.get(onGoingActivity);
						if((Boolean) onGoingActivityMap.get("isOngoing")) {
							onGoingActivityMap.put("isOngoing", false);
							teamsDetailsMap.put("onGoingActivity", "");
						}
						
					}*/
					
					String validActivity = getValidActivityNameForTeam("Team"+(i+1));
					if(!validActivity.isEmpty()) {
						Map<String, Object> activitiesMap = activities.get(validActivity);
						StringBuilder scheduleStringBuilderForTeamI = scheduleStringBuilderList.get(i);
						scheduleStringBuilderForTeamI.append("\n").append(validActivity).append(" ").append(activitiesMap.get("time").toString()).append(" ").append(startTime);					
						activitiesMap.put("isOngoing", true);
						List<String> list = (List<String>) activitiesMap.get("playedByTeamsList");
						list.add("Team"+(i+1));
						activitiesMap.put("playedByTeamsList", list);
						activities.put(validActivity, activitiesMap);
						
						//team details changed -
						
						teamsDetailsMap.put("currentActivityEndsAt", startTime.plus((Integer)activitiesMap.get("time"), ChronoUnit.MINUTES));
												
						List<String> playedActivitiesList = (List<String>) teamsDetailsMap.get("activitiesPlayedList");
						playedActivitiesList.add(validActivity);
						teamsDetailsMap.put("activitiesPlayedList",playedActivitiesList);
						teamsDetailsMap.put("onGoingActivity", validActivity);
						
						teams.put("Team"+(i+1), teamsDetailsMap);
					} else {
						System.out.println("No Valid activity at this time");
					}
					
					
					
				}
				
			}
			
			startTime = startTime.plus(5,ChronoUnit.MINUTES);
			
		}
		
		
		
		for(StringBuilder s : scheduleStringBuilderList) {
			System.out.println(s);
		}
		
		
		
	}
	
	public static void releaseActivitiesDoneAtThisHour(LocalTime startTime) {
		
		Collection<Map<String, Object>> mapCollection = teams.values();
		
		//for(HashMap<String, Object> teamsMap : mapCollection.iterator()) {
		Iterator<Map<String,Object>> itr = mapCollection.iterator();
		while(itr.hasNext()) {
			Map<String,Object> teamsDetailsMap = itr.next();
			LocalTime currentActivityEndsAt = (LocalTime) teamsDetailsMap.get("currentActivityEndsAt");
			if(startTime.compareTo(currentActivityEndsAt) >= 0) {
				
				String onGoingActivity = (String) teamsDetailsMap.get("onGoingActivity");
				if(!onGoingActivity.isEmpty()) {
					Map<String, Object> onGoingActivityMap = activities.get(onGoingActivity);
					if((Boolean) onGoingActivityMap.get("isOngoing")) {
						onGoingActivityMap.put("isOngoing", false);
						teamsDetailsMap.put("onGoingActivity", "");
					}
					
				}
			}		
						
		}		
	}
	
	
	public String getValidActivityNameForTeam(final String teamName) {
		
		List<String> validActivityList = new ArrayList<>();
		
		for(Map.Entry<String, Map<String, Object>> entry : activities.entrySet()) {
			
			Map<String, Object> activityDetailsMap = entry.getValue();
						
			List<String> list = (List<String>)activityDetailsMap.get("playedByTeamsList");
			if(! (Boolean)activityDetailsMap.get("isOngoing") && !(Boolean) list.contains(teamName)){
				validActivityList.add(entry.getKey());
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


}
