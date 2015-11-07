import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimilarityCalculator {
	
	public static double getDistance(List<String> users, List<String> users2, URLRecommendor.Method method)
	{
		switch (method) {
		case COSINE:
			return getCosineDistance(users, users2);
		case EUCLIDEAN:
			return getEuclideanDistance(users, users2);
		case DICE:
			return getDiceDistance(users, users2);
		case JACCARD:
			return getJaccardDistance(users, users2);
		}
		return 0;
	}
	
	public static double getCosineDistance(List<String> users, List<String> users2)
	{
		int magnitude1,magnitude2;
		magnitude1 = users.size();
		magnitude2 = users2.size();
		double mag1,mag2;
		mag1 = Math.sqrt(magnitude1);
		mag2 = Math.sqrt(magnitude2);

		List<String> commonList = new ArrayList<String>(users2);
		commonList.retainAll(users);
		int commonListSize = commonList.size();
		double numerator= commonListSize;
		double denominator = mag1*mag2;
		double cosineDistance = numerator/denominator;

		return cosineDistance;
	}

	public static double getEuclideanDistance(List<String> users, List<String> users2)
	{
		List<String> uncommon = new ArrayList<String> ();
		for (String s : users) {
			if (!users2.contains(users)) uncommon.add(s);
		}
		for (String s : users2) {
			if (!users.contains(users2)) uncommon.add(s);
		}
		int magnitude=uncommon.size();
		double euclideanDistance=Math.sqrt(magnitude);

		return euclideanDistance / Math.sqrt(users.size());   
	}

	public static double getJaccardDistance(List<String> users, List<String> users2)
	{
		List<String> intersectionList = new ArrayList<String>(users2);
		intersectionList.retainAll(users);
		int intersectionListSize = intersectionList.size();
		Set<String> unionList = new HashSet<String>();
		unionList.addAll(users);
		unionList.addAll(users2);
		int unionListSize=unionList.size();
		double jaccardDistance;
		if(unionListSize>0)
		{
			jaccardDistance=intersectionListSize/unionListSize;
			return jaccardDistance;
		}
		else
			return 0;
	}

	public static double getDiceDistance(List<String> users, List<String> users2)
	{
		List<String> intersectionList = new ArrayList<String>(users2);
		intersectionList.retainAll(users);
		int intersectionListSize = 2*(intersectionList.size());
		int magnitude1,magnitude2;
		magnitude1=users.size();
		magnitude2=users2.size();
		double mag1,mag2;
		mag1=Math.sqrt(magnitude1);
		mag2=Math.sqrt(magnitude2);
		double denominator=mag1*mag2;
		double diceDistance=intersectionListSize/denominator;

		return diceDistance;
	}

}
