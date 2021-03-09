package uk.ac.ed.inf.aqmaps;

/**
 * A class contains a static method for checking whether there are errors caused by input arguments and let users understand the error.
 */
public class Error {
	/**
	 * Check if the date or month or year can not convert to integer, print an exception to let users know.
	 * @param date, month, year, latitude, longitude from the input.
	 * @exception An error line which could let users easily understand and exit the application.
	 */
	public static void showUsersError(String date, String month, String year, String latitude, String longitude,
			String randomseed, String port) {
		try{
			Integer.parseInt(date);
		}catch (Exception e) {
			System.out.println("Error: date should be integer but date = " + date);
			System.exit(1);
		}
		try{
			Integer.parseInt(month);
		}catch (Exception e) {
			System.out.println("Error: month should be integer but month = " + month);
			System.exit(1);
		}
		try{
			Integer.parseInt(year);
		}catch (Exception e) {
			System.out.println("Error: year should be integer but year = " + year);
			System.exit(1);
		}

	}

}
