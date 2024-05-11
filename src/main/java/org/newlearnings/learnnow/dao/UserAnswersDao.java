package org.newlearnings.learnnow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.newlearnings.learnnow.delegator.UserAnswersDelegator;
import org.newlearnings.learnnow.model.QuizDetails;
import org.newlearnings.learnnow.model.ShowResults;
import org.newlearnings.learnnow.model.UserAnswers;
import org.newlearnings.learnnow.model.UserResult;
import org.newlearnings.learnnow.util.HttpUrlConnection;

public class UserAnswersDao extends HttpUrlConnection implements UserAnswersDelegator {
	
//	public boolean submitQuizResponses(List<UserAnswers> userAnswers) {
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		int[] rowAddedIndicator = null;
//		boolean isRowAdded = false;
//		
//		try {
//			connection = getHttpUrlConnection();
//			String insertQuery = "insert into USER_ANSWERS (USER_ID, QUIZ_ID, QUESTION_ID, ANSWER) values (?, ?, ?, ?)";
//			preparedStatement = connection.prepareStatement(insertQuery);
//			
//			for (UserAnswers userAnswer : userAnswers) {
//				preparedStatement.setInt(1, userAnswer.getUserId());
//				preparedStatement.setInt(2, userAnswer.getQuizId());
//				preparedStatement.setInt(3, userAnswer.getQuestionId());
//				preparedStatement.setString(4, userAnswer.getAnswer());
//				
//				preparedStatement.addBatch();
//			}
//			
//			rowAddedIndicator = preparedStatement.executeBatch();
//			
//			for (int indicator : rowAddedIndicator) {
//				if (indicator == 1) {
//					isRowAdded = true;
//					break;
//				}
//			}
//			
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//		
//		return isRowAdded;
//	}
//	
	
	public boolean isUserAnswerExists(Connection connection, UserAnswers userAnswer) {
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    boolean exists = false;
	    
	    try {
	        String selectQuery = "SELECT COUNT(*) FROM USER_ANSWERS WHERE USER_ID = ? AND QUIZ_ID = ? AND QUESTION_ID = ?";
	        preparedStatement = connection.prepareStatement(selectQuery);
	        preparedStatement.setInt(1, userAnswer.getUserId());
	        preparedStatement.setInt(2, userAnswer.getQuizId());
	        preparedStatement.setInt(3, userAnswer.getQuestionId());
	        
	        resultSet = preparedStatement.executeQuery();
	        if (resultSet.next()) {
	            int count = resultSet.getInt(1);
	            exists = (count > 0);
	        }
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    } finally {
	        // Close resources
	        // Handle exceptions
	    }
	    
	    return exists;
	}

	
	
	public boolean submitQuizResponses(List<UserAnswers> userAnswers) {
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    boolean isRowUpdated = false;
	    
	    try {
	        connection = getHttpUrlConnection();
	        
	        for (UserAnswers userAnswer : userAnswers) {
	            // Check if the entry already exists in the database
	            if (isUserAnswerExists(connection, userAnswer)) {
	                String updateQuery = "UPDATE USER_ANSWERS SET ANSWER = ? WHERE USER_ID = ? AND QUIZ_ID = ? AND QUESTION_ID = ?";
	                preparedStatement = connection.prepareStatement(updateQuery);
	                preparedStatement.setString(1, userAnswer.getAnswer());
	                preparedStatement.setInt(2, userAnswer.getUserId());
	                preparedStatement.setInt(3, userAnswer.getQuizId());
	                preparedStatement.setInt(4, userAnswer.getQuestionId());
	                preparedStatement.executeUpdate();
	                isRowUpdated = true;
	            } else {
	                // Entry does not exist, perform insert
	                String insertQuery = "INSERT INTO USER_ANSWERS (USER_ID, QUIZ_ID, QUESTION_ID, ANSWER) VALUES (?, ?, ?, ?)";
	                preparedStatement = connection.prepareStatement(insertQuery);
	                preparedStatement.setInt(1, userAnswer.getUserId());
	                preparedStatement.setInt(2, userAnswer.getQuizId());
	                preparedStatement.setInt(3, userAnswer.getQuestionId());
	                preparedStatement.setString(4, userAnswer.getAnswer());
	                preparedStatement.executeUpdate();
	            }
	        }
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    } finally {
	        // Close resources
	        // Handle exceptions
	    }
	    
	    return isRowUpdated;
	}

	
	
	public UserResult showResults(ShowResults showResults) {
		List<QuizDetails> quizDetails = fetchQuizData(showResults.getQuizId(), showResults.getLanguage());
		List<UserAnswers> userAnswers = fetchUserAnswers(showResults.getUserId(), showResults.getQuizId());
		int totalCorrectAnswers = 0;
		
		for (QuizDetails quizDetail : quizDetails) {
			for (UserAnswers userAnswer : userAnswers) {
				if ((quizDetail.getQuizId() == userAnswer.getQuizId()) && 
						(quizDetail.getQuestionId() == userAnswer.getQuestionId()) &&
						(quizDetail.getAnswer().equalsIgnoreCase(userAnswer.getAnswer()))) {
					totalCorrectAnswers++;
				}
			}
		}
		
		int totalquestionsAnswered = userAnswers.size();
		
		UserResult userResult = new UserResult();
		userResult.setTotalCorrectAnswers(totalCorrectAnswers);
		userResult.setTotalQuestionsAnswered(totalquestionsAnswered);
		
		return userResult;
	}
	
	private List<QuizDetails> fetchQuizData(int quizId, String language) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<QuizDetails> quizDetails = new ArrayList<>();
		
		try {
			connection = getHttpUrlConnection();
			String selectQuery = "select QUIZ_ID, LANGUAGES, QUESTION_ID, ANSWER from QUIZ_BANK where "
					+ "QUIZ_ID = ? and LANGUAGES = ?";
			preparedStatement = connection.prepareStatement(selectQuery);
			preparedStatement.setInt(1, quizId);
			preparedStatement.setString(2, language);
			
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				QuizDetails quizDetail = new QuizDetails();
				quizDetail.setQuizId(resultSet.getInt("QUIZ_ID"));
				quizDetail.setLanguages(resultSet.getString("LANGUAGES"));
				quizDetail.setQuestionId(resultSet.getInt("QUESTION_ID"));
				quizDetail.setAnswer(resultSet.getString("ANSWER"));
				
				quizDetails.add(quizDetail);
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return quizDetails;
	}
	
	public List<UserAnswers> fetchUserAnswers(int userId, int quizId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<UserAnswers> userAnswers = new ArrayList<>();
		
		try {
			connection = getHttpUrlConnection();
			String selectQuery = "select * from USER_ANSWERS where USER_ID = ? and QUIZ_ID = ?";
			preparedStatement = connection.prepareStatement(selectQuery);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, quizId);
			
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				UserAnswers userAnswer = new UserAnswers();
				userAnswer.setUserId(resultSet.getInt("USER_ID"));
				userAnswer.setQuizId(resultSet.getInt("QUIZ_ID"));
				userAnswer.setQuestionId(resultSet.getInt("QUESTION_ID"));
				userAnswer.setAnswer(resultSet.getString("ANSWER"));
				
				userAnswers.add(userAnswer);
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return userAnswers;
	}
}
