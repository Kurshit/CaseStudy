package com.delloite.awayday.controller;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.delloite.awayday.service.ScheduleService;

@RestController
public class ScheduleController {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Value("${fileName:}")
	private String filePathAndName;
	
	@RequestMapping(value = "/getschedule/{totalTeams}", method = RequestMethod.GET)
	public String getSchedule(@PathVariable("totalTeams") int totalTeams) throws FileNotFoundException {
	
		scheduleService.parseActivities(filePathAndName);
		scheduleService.createTeams(totalTeams);
		scheduleService.createSchedule(totalTeams);
		
		return null;
		
	}

}
