/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.mup;

import static demo.RankTableDemo.toDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class MeasuringUniversityPerformanceData {
	// Annual Giving
	// Control Institution 2009 2009_NationalRank 2009_ControlRank 2008 2008_NationalRank 2008_ControlRank 2007
	// 2007_NationalRank 2007_ControlRank 2006 2006_NationalRank 2006_ControlRank 2005 2005_NationalRank
	// 2005_ControlRank

	public static class Entry {
		public double value;
		public int nationalRank;
		public int controlRank;

		public Entry(String[] l, int offset) {
			value = toDouble(l, offset++);
			nationalRank = toInt(l, offset++);
			controlRank = toInt(l, offset++);
		}
	}

	public static final int COL_annualGiving = 0;
	public static final int COL_doctoratesAwared = 1;
	public static final int COL_endowment = 2;
	public static final int COL_facultyAwards = 3;
	public static final int COL_federalResearch = 4;
	public static final int COL_nationalAcademyMemberschip = 5;
	public static final int COL_PostdoctoralAppointees = 6;
	public static final int COL_SAT = 7;
	public static final int COL_TotalResearch = 8;

	private final String control;
	private final String state;
	private final String highestDegreeOffered;
	private final String hasAMedicalSchool;
	private final String federalLandGrantInstitution;
	private final String federalResearchFocus;
	private final int totalStudentEnrollmentFall2007;

	private final Entry[][] yearEntries;

	public MeasuringUniversityPerformanceData(String[] l, int years) {
		control = l[0].trim().intern();
		state = l[2].trim().intern();
		highestDegreeOffered = l[3].trim().intern();
		hasAMedicalSchool = l[4].trim().intern();
		federalLandGrantInstitution = l[5].trim().intern();
		federalResearchFocus = l[6].trim().intern();
		totalStudentEnrollmentFall2007 = toInt(l, 7);

		yearEntries = new Entry[years][COL_TotalResearch + 1];
	}

	public static int toInt(String[] l, int o) {
		if (o >= l.length)
			return 0;
		String s = l[o].trim();
		return s.isEmpty() ? 0 : Integer.parseInt(s);
	}

	/**
	 * @return the yearEntries, see {@link #yearEntries}
	 */
	public Entry[][] getYearEntries() {
		return yearEntries;
	}

	/**
	 * @return the control, see {@link #control}
	 */
	public String getControl() {
		return control;
	}


	/**
	 * @return the state, see {@link #state}
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the highestDegreeOffered, see {@link #highestDegreeOffered}
	 */
	public String getHighestDegreeOffered() {
		return highestDegreeOffered;
	}

	/**
	 * @return the hasAMedicalSchool, see {@link #hasAMedicalSchool}
	 */
	public String getHasAMedicalSchool() {
		return hasAMedicalSchool;
	}

	/**
	 * @return the federalLandGrantInstitution, see {@link #federalLandGrantInstitution}
	 */
	public String getFederalLandGrantInstitution() {
		return federalLandGrantInstitution;
	}

	/**
	 * @return the federalResearchFocus, see {@link #federalResearchFocus}
	 */
	public String getFederalResearchFocus() {
		return federalResearchFocus;
	}

	/**
	 * @return the totalStudentEnrollmentFall2007, see {@link #totalStudentEnrollmentFall2007}
	 */
	public int getTotalStudentEnrollmentFall2007() {
		return totalStudentEnrollmentFall2007;
	}

	public static StackedRankColumnModel addYear(RankTableModel table, String title, Function<IRow, Entry[]> year) {
		StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.setTitle(title);

		stacked.add(col(year, COL_annualGiving, "Annual Giving", "#FC9272", "#FEE0D2"));
		stacked.add(col(year, COL_doctoratesAwared, "Doctorates Awared", "#9ECAE1", "#DEEBF7"));
		stacked.add(col(year, COL_endowment, "Endowment", "#A1D99B", "#E5F5E0"));
		stacked.add(col(year, COL_facultyAwards, "Faculty Awards", "#C994C7", "#E7E1EF"));
		stacked.add(col(year, COL_federalResearch, "Federal Research", "#FDBB84", "#FEE8C8"));
		stacked.add(col(year, COL_nationalAcademyMemberschip, "National Academy Membership", "#DFC27D", "#F6E8C3"));

		stacked.add(col(year, COL_PostdoctoralAppointees, "Postdoctoral Appointees", "#DFC27D", "#F6E8C3"));
		stacked.add(col(year, COL_SAT, "SAT", "#DFC27D", "#F6E8C3"));
		stacked.add(col(year, COL_TotalResearch, "Total Research", "#DFC27D", "#F6E8C3"));

		stacked.setWidth(400);

		return stacked;
	}

	private static DoubleRankColumnModel col(Function<IRow, Entry[]> year, int col, String text, String color,
			String bgColor) {
		return new DoubleRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				new Color(color), new Color(bgColor), unbound(), DoubleInferrers.MEDIAN,
				NumberFormat.getIntegerInstance(Locale.ENGLISH));
	}

	protected static PiecewiseMapping unbound() {
		return new PiecewiseMapping(0, Float.NaN);
	}

	public static Map<String, MeasuringUniversityPerformanceData> readData(int... years) throws IOException {
		Map<String, MeasuringUniversityPerformanceData> data = new LinkedHashMap<>();
		//read general data
		try (BufferedReader r = new BufferedReader(new InputStreamReader(MeasuringUniversityPerformanceData.class.getResourceAsStream("InstitutionalCharacteristics.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine(); // header
			r.readLine(); // header2
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				if (l.length == 0)
					continue;
				String school = l[1];
				data.put(school, new MeasuringUniversityPerformanceData(l,years.length));
			}
		}
		List<String> names = Arrays.asList("AnnualGiving.txt", "Doctorates.txt", "Endowment.txt", "FacultyAwards.txt",
				"FederalResearch.txt", "NationalAcademyMembership.txt", "NationalMeritAndAchievements.txt",
				"PostdoctoralAppointees.txt", "SAT.txt", "TotalResearch.txt");
		for (int i = 0; i <= COL_TotalResearch; ++i) {
			String name = names.get(i);
			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					MeasuringUniversityPerformanceData.class.getResourceAsStream(name), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				r.readLine(); // header2
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					if (l.length == 0)
						continue;
					String school = l[1];
					MeasuringUniversityPerformanceData row = data.get(school);
					int offset = 2;
					for (int j = 0; j < years.length; ++j) {
						Entry[] entries = row.yearEntries[j];
						entries[i] = new Entry(l, offset);
						offset += 3;
					}
				}
			}
		}
		return data;
	}

	static class ValueGetter extends ADoubleFunction<IRow> {
		private final Function<IRow, Entry[]> year;
		private final int type;

		public ValueGetter(Function<IRow, Entry[]> year, int type) {
			this.year = year;
			this.type = type;
		}

		@Override
		public double applyPrimitive(IRow in) {
			Entry[] y = year.apply(in);
			if (y == null || y.length <= type)
				return Double.NaN;
			Entry e = y[type];
			return e.value;
		}
	}
}
