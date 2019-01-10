package com.delloite.awayday.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
				System.out.println(activityName + " " + minutes);
				activityDetails.put("time", minutes);
				activityDetails.put("isOngoing", false);
				activityDetails.put("playedByTeamsList", new ArrayList<String>());
				activities.put(activityName, activityDetails );
			});

			System.out.println(activities);
		} catch (IOException e) {
			e.printStackTrace();
		}		

	}

	public void createTeams(final int totalTeams) {

		for(int i = 1; i<= totalTeams; i++) {

			Map<String, Object> teamDetails = new HashMap<>();
			teamDetails.put("isFree", true);
			teamDetails.put("currentActivityEndsIn", 0);
			teams.put("Team"+i, teamDetails);			
		}	

	}

	public void createSchedule(int totalTeams) {
			
		LocalTime startTime = LocalTime.parse("09:00");
		
		for(int i=1; i<= totalTeams; i++) {
			
			if((Boolean) teams.get("Team"+i).get("isFree")) {
				
			}
			
		}
		
		
		
	}


}
