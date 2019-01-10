package com.delloite.awayday.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.delloite.awayday.service.ScheduleService;

@RestController
public class ScheduleController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@RequestMapping(value = "/getschedule/{totalTeams}", method = RequestMethod.GET)
	public String getSchedule(@PathVariable("totalTeams") int totalTeams) {
		
		String fileName = "./activities.txt";
		scheduleService.parseActivities(fileName);
		scheduleService.createTeams(totalTeams);
		
		return null;
		
	}

}
