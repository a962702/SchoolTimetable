import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fitness {
	
	static HashMap<String,Integer[][]> notArrange;
	
	static {
		notArrange = new HashMap<>();
		notArrange.put("王", new Integer[][] {{2,1,4},{5,6,9}});
		notArrange.put("賴", new Integer[][] {{2,1,4},{3,1,4}});
		notArrange.put("孔", new Integer[][] {{1,1,4},{5,1,4}});
		notArrange.put("顧", new Integer[][] {{5,6,9},{5,1,4}});
		notArrange.put("張", new Integer[][] {{1,1,4},{5,1,4}});
		notArrange.put("黃", new Integer[][] {{5,1,4},{5,6,9}});
		notArrange.put("徐", new Integer[][] {{1,1,4},{5,6,9}});
		notArrange.put("宜", new Integer[][] {{1,1,4},{5,6,9}});
		notArrange.put("宗", new Integer[][] {{4,6,9},{5,6,9}});
	}
	
	static int getValue(Curriculum curriculum) {
		ArrayList<Course> courses = curriculum.courses;
		int fitnessValue = 100;
		Course c1, c2;
		int[][] hour = new int[6][6];
		for (int i=0;i<courses.size();i++) {
			c1 = courses.get(i);
			
			// count how many hours of lessons a day students have to attend
			hour[c1.grade][c1.week] += c1.hour;
			
			// check if the time of the class can't be scheduled
			Integer[][] time = notArrange.get(c1.teacher);
			if (time!=null && 
				((time[0][0]==c1.week && 
				 ((time[0][1]<=c1.end && time[0][1]>=c1.start) || (time[0][2]>=c1.start && time[0][2]<=c1.end))) ||
				 (time[1][0]==c1.week && 
				 ((time[1][1]<=c1.end && time[1][1]>=c1.start) || (time[1][2]>=c1.start && time[1][2]<=c1.end)))))
				fitnessValue -= 100;
			
			// check 黃 宜 徐 have class in Tuesday
			if ((c1.teacher.equals("黃") || c1.teacher.equals("宜") || c1.teacher.equals("徐"))
					&& c1.week==2) fitnessValue -= 100;
			
			// check if 賴 course is in afternoon
			if (c1.teacher.equals("賴") && c1.start>=5) fitnessValue += 10;
			
			// check course with 3 HR should start at 2 or 6
			if (!c1.isFixed() && c1.end - c1.start + 1== 3 && !(c1.start == 2 || c1.start == 6)) fitnessValue -= 30;
			
			// check course with 2 HR should start at 3 or 6 or 8
			if (!c1.isFixed() && c1.end - c1.start + 1== 2 && !(c1.start == 3 || c1.start == 6 || c1.start == 8)) fitnessValue -= 30;
			
			for (int j=i+1;j<courses.size();j++) {
				c2 = courses.get(j);
				// check if c1 course and c2 course overlapping
				if (c1.week==c2.week && 
					((c1.start<=c2.end && c1.start>=c2.start) || (c1.end>=c2.start && c1.end<=c2.end))) {
					// check if one or both of c1 and c2 is fixed course and same grade 
					if ((c1.grade==c2.grade) && (c1.isFixed() || c2.isFixed())) fitnessValue -= 100;
					
					// check if one or both of c1 and c2 is major course and same grade
					if ((c1.grade==c2.grade) && (c1.isMajor() || c2.isMajor())) fitnessValue -= 100; 
					
					// check if c1 and c2 are both major courses and grade are adjacent
					if (!(c1.isFixed() && c2.isFixed()) && Math.abs(c1.grade-c2.grade)==1 && (c1.isMajor() && c2.isMajor())) fitnessValue -= 100;
					
					// check electives in same grade
					if ((c1.grade==c2.grade) && (!c1.isMajor() && !c2.isMajor())) fitnessValue -= 30;
					
					// check if c1 and c2 are the same teacher
					if (!(c1.isFixed() && c2.isFixed()) && c1.teacher.equals(c2.teacher)) fitnessValue -= 100;
				}
			}
		}
		// check if students have class for more than six hours a day
		for (int i=0;i<hour.length;i++) {
			for (int j=0;j<hour[i].length;j++) {
				if (hour[i][j]>6) fitnessValue -= 10;
			}
		}
		return fitnessValue;
	}
	
	static void print() {
		for (Map.Entry<String,Integer[][]> entry : notArrange.entrySet()) {
			System.out.print(entry.getKey() + " ");
			for (Integer[] i : entry.getValue()) {
				System.out.print("[");
				for (Integer j : i) {
					System.out.print(j + ", ");
				}
				System.out.print("], ");
			}
			System.out.println();
		}
	}
}
