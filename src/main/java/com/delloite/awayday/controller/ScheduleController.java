package com.delloite.awayday.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {
	
	@RequestMapping(value = "/getschedule/{totalTeams}", method = RequestMethod.GET)
	public String getSchedule(@PathVariable("totalTeams") int totalTeams) {
		
		String fileName = "./activities.txt";
		List<String> list = new ArrayList<>();
		
		Map<String, Map<String, Object>> activities = new HashMap<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			
			stream.forEach(line -> {
				Map<String, Object> activityDetails = new HashMap<>();
				int minIndex = line.indexOf("min");
				String activityName = minIndex !=-1 ? line.substring(0,minIndex - 2).trim() : line.substring(0,line.indexOf("sprint") - 2).trim();
				int minutes = minIndex !=-1 ? Integer.parseInt(line.substring(minIndex - 2, minIndex).trim()) : 15;
				System.out.println(activityName + " " + minutes);
				activityDetails.put("time", minutes);
				activityDetails.put("isOngoing", false);
				activities.put(activityName, activityDetails );
			});
					
			System.out.println(activities);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(list);
		
		return null;
		
	}

}
