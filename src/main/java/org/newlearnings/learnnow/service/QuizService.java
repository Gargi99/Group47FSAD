package org.newlearnings.learnnow.service;

import java.util.List;

import org.newlearnings.learnnow.dao.QuizDao;
import org.newlearnings.learnnow.delegator.QuizDelegator;
import org.newlearnings.learnnow.model.QuizDetails;

import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;





@Path("/quiz")
public class QuizService {
	QuizDelegator quizDaoImpl = new QuizDao();
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getQuizDetails")
	public String getQuizDetailsByLanguage(@QueryParam(value = "language") String language) {
		Gson gson = new Gson();
		List<QuizDetails> quizDetails = quizDaoImpl.getQuizDetailsByLanguage(language);
		String quizData = gson.toJson(quizDetails);
		
		return quizData;
	}
}
