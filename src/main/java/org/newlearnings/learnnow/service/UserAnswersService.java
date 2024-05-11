package org.newlearnings.learnnow.service;

import java.util.List;

import org.newlearnings.learnnow.dao.UserAnswersDao;
import org.newlearnings.learnnow.delegator.UserAnswersDelegator;
import org.newlearnings.learnnow.model.ShowResults;
import org.newlearnings.learnnow.model.UserAnswers;

import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/userAnswers")
public class UserAnswersService {
	UserAnswersDelegator userAnswersDaoImpl = new UserAnswersDao();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/submitQuizResponses")
	public Response submitQuizResponses(List<UserAnswers> userAnswers) {
		System.out.println(userAnswers);
		if (userAnswersDaoImpl.submitQuizResponses(userAnswers)) {
			return Response.ok().entity("All answers submitted successfully.").build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Some answers were not saved.").build();
		}
	}
	
//	@GET
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/showResults")
//	public String showResults(ShowResults showResults) {
//		Gson gson = new Gson();
//		return gson.toJson(userAnswersDaoImpl.showResults(showResults));
//	}
	
	@GET
    @Path("/showResults")
    @Produces(MediaType.APPLICATION_JSON)
    public String showResults(
        @QueryParam("userId") int userId,
        @QueryParam("language") String language,
        @QueryParam("quizId") int quizId
    ) {
        ShowResults showResults = new ShowResults();
        showResults.setUserId(userId);
        showResults.setLanguage(language);
        showResults.setQuizId(quizId);
        
        Gson gson = new Gson();
        return gson.toJson(userAnswersDaoImpl.showResults(showResults));
    }
	
}
