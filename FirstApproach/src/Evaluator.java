import java.util.List;

public class Evaluator {

	public static double getRMSError(List<Integer> actualValues, List<Integer> scores)
	{
		double RMSE = 0;
		
		for(int i=0; i< actualValues.size(); i++)
		{
			RMSE = RMSE + Math.pow((actualValues.get(i) - scores.get(i)), 2);
		}
		
		// normalize
		RMSE = RMSE / actualValues.size();
		RMSE = Math.sqrt(RMSE);
		
		return RMSE;
	}
}
