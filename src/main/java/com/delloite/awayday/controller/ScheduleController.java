package com.delloite.awayday.controller;

import java.io.FileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.delloite.awayday.exception.EmptyInputFileException;
import com.delloite.awayday.exception.TotalNumberOfTeamsExceedsActivitiesException;
import com.delloite.awayday.service.ScheduleService;

/**
 * The main REST controller that maps the requests and send back response with generated schedule.
 * 
 * @author Kurshit Kukreja
 *
 */

@RestController
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@Value("${fileName:}")
	private String filePathAndName;

	/**
	 * 
	 * @param totalTeams the total number of teams participating
	 * @return the generated schedule for each team as a string
	 * @throws FileNotFoundException If the input file of activities is not present on buildpath
	 */

	@RequestMapping(value = "/getschedule/{totalTeams}", method = RequestMethod.GET)
	public ResponseEntity<String> getSchedule(@PathVariable("totalTeams") int totalTeams) throws FileNotFoundException {
		String response = null;
		try
		{
			scheduleService.parseActivities(filePathAndName);
			scheduleService.createTeams(totalTeams);
			response = scheduleService.createSchedule(totalTeams);
		} catch(EmptyInputFileException e) {
			response = "Input file of activities is empty";
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response);
		} catch(TotalNumberOfTeamsExceedsActivitiesException e) {
			response = e.getMessage();
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response);
		}
		
		
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(response);
	}

}
